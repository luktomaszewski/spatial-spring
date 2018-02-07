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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService = new LocationService(locationRepository);

    @Test
    public void existsShouldReturnTrue() throws Exception {
        // given
        Long id = 1L;

        when(locationRepository.exists(id)).thenReturn(true);

        // when
        boolean result = locationService.exists(id);

        // then
        verify(locationRepository, atLeastOnce()).exists(id);
        assertTrue(result);
    }

    @Test
    public void existsShouldReturnFalse() throws Exception {
        // given
        Long id = 1L;

        when(locationRepository.exists(id)).thenReturn(false);

        // when
        boolean result = locationService.exists(id);

        // then
        verify(locationRepository, atLeastOnce()).exists(id);
        assertFalse(result);
    }

    @Test
    public void saveLocationShouldReturnId() throws Exception {
        // given
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "test");
        Feature feature = new Feature(geoJson, properties);

        Long id = 1L;

        when(locationRepository.save(any(LocationEntity.class)))
                .thenAnswer((Answer<LocationEntity>) invocation -> {
                    LocationEntity entity = (LocationEntity) invocation.getArguments()[0];
                    entity.setId(id);
                    return entity;
                });

        // when
        Long result = locationService.saveLocation(feature);

        // then
        assertThat(result).isEqualTo(id);
        verify(locationRepository, atLeastOnce()).save(any(LocationEntity.class));
    }

    @Test
    public void updateLocation() throws Exception {
        // given
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "test");
        Feature feature = new Feature(geoJson, properties);

        Long id = 1L;

        when(locationRepository.save(any(LocationEntity.class)))
                .thenAnswer((Answer<LocationEntity>) invocation -> {
                    LocationEntity entity = (LocationEntity) invocation.getArguments()[0];
                    entity.setId(id);
                    return entity;
                });

        // when
        locationService.updateLocation(id, feature);

        // then
        verify(locationRepository, atLeastOnce()).save(any(LocationEntity.class));
    }

    @Test
    public void deleteLocation() throws Exception {
        // given
        Long id = 1L;

        // when
        locationService.deleteLocation(id);

        // then
        verify(locationRepository, atLeastOnce()).delete(id);
    }

    @Test
    public void findLocationByIdShouldReturnOptionalEmpty() throws Exception {
        // given
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(null);

        // when
        Optional<Feature> result = locationService.findLocationById(id);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    public void findLocationByIdShouldReturnOptionalFeature() throws Exception {
        // given
        Long id = 1L;
        String name = "test";
        Point geometry = new GeometryFactory().createPoint(new Coordinate(0, 0));

        Geometry geoJson = new GeoJSONWriter().write(geometry);
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        Feature feature = new Feature(id, geoJson, properties);

        LocationEntity entity = new LocationEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setGeometry(geometry);

        when(locationRepository.findById(id)).thenReturn(entity);

        // when
        Optional<Feature> result = locationService.findLocationById(id);

        // then
        assertThat(feature).isEqualToComparingFieldByFieldRecursively(result.get());
    }

    @Test
    public void findAllLocations() throws Exception {
        // given
        Long id = 1L;
        String name = "test";
        Point geometry = new GeometryFactory().createPoint(new Coordinate(0, 0));

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

        when(locationRepository.findAll()).thenReturn(locationEntityList);

        // when
        FeatureCollection result = locationService.findAllLocations();

        // then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(featureCollection);
    }

    @Test
    public void findAllLocationsWithin() throws Exception {
        // given
        Long id = 1L;
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

        when(locationRepository.findWithin(any(com.vividsolutions.jts.geom.Geometry.class))).thenReturn(locationEntityList);

        // when
        FeatureCollection result = locationService.findAllLocationsWithin(filterGeoJson);

        // then
        assertThat(featureCollection).isEqualToComparingFieldByFieldRecursively(result);
    }
}