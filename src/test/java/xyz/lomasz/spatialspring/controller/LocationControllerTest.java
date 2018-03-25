package xyz.lomasz.spatialspring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.geojson.Geometry;
import xyz.lomasz.spatialspring.service.LocationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController = new LocationController(locationService);

    @Test
    public void postLocationShouldReturnHttpStatusCreated() {
        // given
        Long id = 1L;
        String userId = "xyz";
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        Feature feature = new Feature(geoJson, properties);

        when(locationService.saveLocation(userId, feature)).thenReturn(id);

        // when
        ResponseEntity<?> result = locationController.postLocation(userId, feature);

        // then
        verify(locationService, atLeastOnce()).saveLocation(userId, feature);
        assertEquals("/location/" + id, result.getHeaders().getLocation().toString());
        assertThat(result.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void getLocationByIdShouldReturnHttpStatusOk() {
        // given
        Long id = 1L;
        String userId = "xyz";
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        Feature feature = new Feature(geoJson, properties);

        when(locationService.findLocationById(userId, id)).thenReturn(Optional.of(feature));

        // when
        ResponseEntity<?> result = locationController.getLocationById(userId, id);

        // then
        assertThat(result.getBody(), is(feature));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void getLocationByIdShouldReturnHttpStatusNotFound() {
        // given
        Long id = 1L;
        String userId = "xyz";

        when(locationService.findLocationById(userId, id)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> result = locationController.getLocationById(userId, id);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void putLocationShouldReturnHttpStatusOk() {
        // given
        Long id = 1L;
        String userId = "xyz";
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        Feature feature = new Feature(geoJson, properties);

        when(locationService.exists(userId, id)).thenReturn(true);

        // when
        ResponseEntity<?> result = locationController.putLocation(userId, id, feature);

        // then
        verify(locationService, atLeastOnce()).updateLocation(userId, id, feature);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void putLocationShouldReturnHttpStatusNotFound() {
        // given
        Long id = 1L;
        String userId = "xyz";
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        Feature feature = new Feature(geoJson, properties);

        when(locationService.exists(userId, id)).thenReturn(false);

        // when
        ResponseEntity<?> result = locationController.putLocation(userId, id, feature);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteLocationShouldReturnHttpStatusOk() {
        // given
        Long id = 1L;
        String userId = "xyz";

        when(locationService.exists(userId, id)).thenReturn(true);

        // when
        ResponseEntity<?> result = locationController.deleteLocation(userId, id);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void deleteLocationShouldReturnHttpStatusNotFound() {
        // given
        Long id = 1L;
        String userId = "xyz";

        when(locationService.exists(userId, id)).thenReturn(false);

        // when
        ResponseEntity<?> result = locationController.deleteLocation(userId, id);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getAllLocationsShouldReturnFeatureCollection() {
        // given
        String userId = "xyz";
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        Feature feature = new Feature(geoJson, properties);

        Feature[] features = {feature};
        FeatureCollection featureCollection = new FeatureCollection(features);

        when(locationService.findAllLocations(userId)).thenReturn(featureCollection);

        // when
        ResponseEntity<?> result = locationController.getAllLocations(userId);

        // then
        assertThat(result.getBody(), is(featureCollection));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void getLocationsByGeometryShouldReturnFeatureCollection() {
        // given
        String userId = "xyz";
        String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [125.6, 10.1]}";
        Geometry geoJson = (Geometry) GeoJSONFactory.create(geoJsonString);
        Map<String, Object> properties = new HashMap<>();
        Feature feature = new Feature(geoJson, properties);

        Feature[] features = {feature};
        FeatureCollection featureCollection = new FeatureCollection(features);

        when(locationService.findAllLocationsWithin(userId, geoJson)).thenReturn(featureCollection);

        // when
        ResponseEntity<?> result = locationController.getLocationsByGeometry(userId, geoJson);

        // then
        assertThat(result.getBody(), is(featureCollection));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }
}