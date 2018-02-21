package xyz.lomasz.spatialspring.service;

import com.vividsolutions.jts.geom.Geometry;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import xyz.lomasz.spatialspring.domain.entity.LocationEntity;
import xyz.lomasz.spatialspring.repository.LocationRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static xyz.lomasz.spatialspring.helper.GeometryHelper.convertGeoJsonToJtsGeometry;
import static xyz.lomasz.spatialspring.helper.GeometryHelper.convertJtsGeometryToGeoJson;

@Service
@CommonsLog
public class LocationService {

    private LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public boolean exists(Long id) {
        return locationRepository.exists(id);
    }

    public Long saveLocation(Feature feature) {
        LocationEntity locationEntity = convertFeatureToEntity(feature);
        locationRepository.save(locationEntity);
        return locationEntity.getId();
    }

    public void updateLocation(Long id, Feature feature) {
        LocationEntity locationEntity = convertFeatureToEntity(feature);
        locationEntity.setId(id);
        locationRepository.save(locationEntity);
    }

    public void deleteLocation(Long id) {
        locationRepository.delete(id);
    }

    public Optional<Feature> findLocationById(Long id) {
        LocationEntity locationEntity = locationRepository.findById(id);
        if (locationEntity == null) {
            return Optional.empty();
        }
        return Optional.of(convertEntityToFeature(locationEntity));
    }

    public FeatureCollection findAllLocations() {
        List<LocationEntity> locationEntityList = locationRepository.findAll();
        Feature[] features = mapEntityListToFeatures(locationEntityList);
        return new FeatureCollection(features);
    }

    public FeatureCollection findAllLocationsWithin(org.wololo.geojson.Geometry geoJson) {
        Geometry geometry = convertGeoJsonToJtsGeometry(geoJson);
        List<LocationEntity> locationEntityList = locationRepository.findWithin(geometry);
        Feature[] features = mapEntityListToFeatures(locationEntityList);
        return new FeatureCollection(features);
    }

    private Feature[] mapEntityListToFeatures(List<LocationEntity> locationEntityList) {
        return locationEntityList.stream()
                .map(this::convertEntityToFeature)
                .toArray(Feature[]::new);
    }

    private LocationEntity convertFeatureToEntity(Feature feature) {
        LocationEntity entity = new LocationEntity();
        Map<String, Object> propertiesList = feature.getProperties();
        Arrays.stream(LocationEntity.class.getDeclaredFields())
                .filter(i -> !i.isSynthetic())
                .forEach(i -> {
                    try {
                        Field f = LocationEntity.class.getDeclaredField(i.getName());
                        f.setAccessible(true);
                        f.set(entity, propertiesList.getOrDefault(i.getName(), null));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        log.warn(e.getMessage());
                    }
                });
        entity.setGeometry(convertGeoJsonToJtsGeometry(feature.getGeometry()));
        return entity;
    }

    private Feature convertEntityToFeature(LocationEntity entity) {
        Long id = entity.getId();
        org.wololo.geojson.Geometry geometry = convertJtsGeometryToGeoJson(entity.getGeometry());

        Map<String, Object> properties = new HashMap<>();
        Arrays.stream(LocationEntity.class.getDeclaredFields())
                .filter(i -> !i.isSynthetic())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        if (field.getType() != Geometry.class && !field.getName().equals("id")) {
                            properties.put(field.getName(), field.get(entity));
                        }
                    } catch (IllegalAccessException e) {
                        log.warn(e.getMessage());
                    }
                });

        return new Feature(id, geometry, properties);
    }


}
