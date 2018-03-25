package xyz.lomasz.spatialspring.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.geojson.Geometry;
import org.wololo.jts2geojson.GeoJSONWriter;
import xyz.lomasz.spatialspring.domain.entity.LocationEntity;
import xyz.lomasz.spatialspring.repository.LocationRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService = new LocationService(locationRepository);

    @Test
    public void existsShouldReturnTrue() {
        // given
        Long id = 1L;
        String userId = "xyz";

        when(locationRepository.existsLocationEntityByUserAndId(userId, id)).thenReturn(true);

        // when
        boolean result = locationService.exists(userId, id);

        // then
        verify(locationRepository, atLeastOnce()).existsLocationEntityByUserAndId(userId, id);
        assertTrue(result);
    }

    @Test
    public void existsShouldReturnFalse() {
        // given
        Long id = 1L;
        String userId = "xyz";

        when(locationRepository.existsLocationEntityByUserAndId(userId, id)).thenReturn(false);

        // when
        boolean result = locationService.exists(userId, id);

        // then
        verify(locationRepository, atLeastOnce()).existsLocationEntityByUserAndId(userId, id);
        assertFalse(result);
    }

    @Test
    public void saveLocationShouldReturnId() {
        // given
        Long id = 1L;
        String userId = "xyz";

        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "test");
        Feature feature = new Feature(geoJson, properties);

        when(locationRepository.save(any(LocationEntity.class)))
                .thenAnswer((Answer<LocationEntity>) invocation -> {
                    LocationEntity entity = (LocationEntity) invocation.getArguments()[0];
                    entity.setId(id);
                    entity.setUser(userId);
                    return entity;
                });

        // when
        Long result = locationService.saveLocation(userId, feature);

        // then
        assertThat(result).isEqualTo(id);
        verify(locationRepository, atLeastOnce()).save(any(LocationEntity.class));
    }

    @Test
    public void updateLocation() {
        // given
        Long id = 1L;
        String userId = "xyz";

        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "test");
        Feature feature = new Feature(geoJson, properties);

        when(locationRepository.save(any(LocationEntity.class)))
                .thenAnswer((Answer<LocationEntity>) invocation -> {
                    LocationEntity entity = (LocationEntity) invocation.getArguments()[0];
                    entity.setId(id);
                    entity.setUser(userId);
                    return entity;
                });

        // when
        locationService.updateLocation(userId, id, feature);

        // then
        verify(locationRepository, atLeastOnce()).save(any(LocationEntity.class));
    }

    @Test
    public void deleteLocation() {
        // given
        Long id = 1L;
        String userId = "xyz";

        // when
        locationService.deleteLocation(userId, id);

        // then
        verify(locationRepository, atLeastOnce()).delete(id);
    }

    @Test
    public void findLocationByIdShouldReturnOptionalEmpty() {
        // given
        Long id = 1L;
        String userId = "xyz";

        when(locationRepository.findByUserAndId(userId, id)).thenReturn(Optional.empty());

        // when
        Optional<Feature> result = locationService.findLocationById(userId, id);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    public void findLocationByIdShouldReturnOptionalFeature() {
        // given
        Long id = 1L;
        String userId = "xyz";
        String name = "test";
        Point geometry = new GeometryFactory().createPoint(new Coordinate(0, 0));

        Geometry geoJson = new GeoJSONWriter().write(geometry);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        Feature feature = new Feature(id, geoJson, properties);

        LocationEntity entity = new LocationEntity();
        entity.setId(id);
        entity.setUser(userId);
        entity.setName(name);
        entity.setGeometry(geometry);

        when(locationRepository.findByUserAndId(userId, id)).thenReturn(Optional.of(entity));

        // when
        Optional<Feature> result = locationService.findLocationById(userId, id);

        // then
        assertThat(feature).isEqualToComparingFieldByFieldRecursively(result.get());
    }

    @Test
    public void findAllLocations() {
        // given
        Long id = 1L;
        String userId = "xyz";
        String name = "test";
        Point geometry = new GeometryFactory().createPoint(new Coordinate(0, 0));

        LocationEntity entity = new LocationEntity();
        entity.setId(id);
        entity.setUser(userId);
        entity.setName(name);
        entity.setGeometry(geometry);
        List<LocationEntity> locationEntityList = Collections.singletonList(entity);

        Geometry geoJson = new GeoJSONWriter().write(geometry);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        Feature feature = new Feature(id, geoJson, properties);
        FeatureCollection featureCollection = new FeatureCollection(new Feature[]{feature});

        when(locationRepository.findAllByUser(userId)).thenReturn(locationEntityList);

        // when
        FeatureCollection result = locationService.findAllLocations(userId);

        // then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(featureCollection);
    }

    @Test
    public void findAllLocationsWithin() {
        // given
        Long id = 1L;
        String userId = "xyz";
        String name = "test";
        Point geometry = new GeometryFactory().createPoint(new Coordinate(10, 10));

        LocationEntity entity = new LocationEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setGeometry(geometry);
        List<LocationEntity> locationEntityList = Collections.singletonList(entity);

        Geometry geoJson = new GeoJSONWriter().write(geometry);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        Feature feature = new Feature(id, geoJson, properties);
        FeatureCollection featureCollection = new FeatureCollection(new Feature[]{feature});

        String filterGeoJsonString = "{ \"type\": \"Polygon\", \"coordinates\": [" +
                "[[30, 10], [40, 40], [20, 40], [10, 20], [30, 10]]\n" +
                "]}";

        Geometry filterGeoJson = (Geometry) GeoJSONFactory.create(filterGeoJsonString);

        when(locationRepository.findWithin(any(), any(com.vividsolutions.jts.geom.Geometry.class))).thenReturn(locationEntityList);

        // when
        FeatureCollection result = locationService.findAllLocationsWithin(userId, filterGeoJson);

        // then
        assertThat(featureCollection).isEqualToComparingFieldByFieldRecursively(result);
    }
}