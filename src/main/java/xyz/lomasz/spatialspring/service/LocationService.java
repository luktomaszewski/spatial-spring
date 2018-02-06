package xyz.lomasz.spatialspring.service;

import com.vividsolutions.jts.geom.Geometry;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;
import xyz.lomasz.spatialspring.domain.entity.LocationEntity;
import xyz.lomasz.spatialspring.repository.LocationRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@CommonsLog
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

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

        Feature[] features = locationEntityList.stream()
                .map(this::convertEntityToFeature)
                .toArray(Feature[]::new);

        return new FeatureCollection(features);
    }

    public FeatureCollection findAllLocationsByGeometry(Geometry geometry) {
        List<LocationEntity> locationEntityList = locationRepository.findWithin(geometry);

        Feature[] features = locationEntityList.stream()
                .map(this::convertEntityToFeature)
                .toArray(Feature[]::new);

        return new FeatureCollection(features);

    }

    public LocationEntity convertFeatureToEntity(Feature feature) {
        LocationEntity entity = new LocationEntity();
        Map<String, Object> propertiesList = feature.getProperties();
        Arrays.asList(LocationEntity.class.getDeclaredFields())
                .forEach(i -> {
                    try {
                        Field f = LocationEntity.class.getDeclaredField(i.getName());
                        f.setAccessible(true);
                        f.set(entity, propertiesList.getOrDefault(i.getName(), null));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new IllegalArgumentException();
                    }
                });
        entity.setGeometry(convertGeoJsonToGeometry(feature.getGeometry()));
        return entity;
    }

    public Feature convertEntityToFeature(LocationEntity entity) {
        Long id = entity.getId();
        org.wololo.geojson.Geometry geometry = convertGeometryToGeoJson(entity.getGeometry());

        Map<String, Object> properties = new HashMap<String, Object>();
        List<Field> fieldList = Arrays.asList(LocationEntity.class.getDeclaredFields());
        fieldList
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                            if (field.getType() != Geometry.class && field.getName() != "id") {
                                properties.put(field.getName(), field.get(entity));
                            }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        return new Feature(id, geometry, properties);
    }

    public org.wololo.geojson.Geometry convertGeometryToGeoJson(Geometry geometry) {
        GeoJSONWriter writer = new GeoJSONWriter();
        return writer.write(geometry);
    }

    public Geometry convertGeoJsonToGeometry(org.wololo.geojson.Geometry geoJson) {
        GeoJSONReader reader = new GeoJSONReader();
        return reader.read(geoJson);
    }
}
