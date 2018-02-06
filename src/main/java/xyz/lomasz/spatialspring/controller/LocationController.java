package xyz.lomasz.spatialspring.controller;

import com.vividsolutions.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import xyz.lomasz.spatialspring.service.LocationService;

import java.util.Optional;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    @RequestMapping(value ="/location/", method = RequestMethod.POST)
    public ResponseEntity postLocation(@RequestBody Feature feature) {
        Long id = locationService.saveLocation(feature);

        UriComponentsBuilder ucBuilder = UriComponentsBuilder.newInstance();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/location/{id}").buildAndExpand(id).toUri());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/location/{id}", method = RequestMethod.GET)
    public ResponseEntity getLocationById(@PathVariable("id") Long id) {
        Optional<Feature> location = locationService.findLocationById(id);
        return location.map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/location/{id}", method = RequestMethod.PUT)
    public ResponseEntity putLocation(@PathVariable("id") Long id, @RequestBody Feature feature) {
        if (!locationService.exists(id)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        locationService.updateLocation(id, feature);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/location/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteLocation(@PathVariable("id") Long id) {
        if (!locationService.exists(id)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        locationService.deleteLocation(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/locations/", method = RequestMethod.GET)
    public ResponseEntity<FeatureCollection> getAllLocations() {
        return new ResponseEntity<>(locationService.findAllLocations(), HttpStatus.OK);
    }

    @RequestMapping(value = "/locations/within", method = RequestMethod.POST)
    public ResponseEntity<FeatureCollection> getLocationsByGeometry(@RequestBody org.wololo.geojson.Geometry geoJson) {
        Geometry geometry = locationService.convertGeoJsonToGeometry(geoJson);
        return new ResponseEntity<>(locationService.findAllLocationsByGeometry(geometry), HttpStatus.OK);
    }
}
