package xyz.lomasz.spatialspring.controller;

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
import xyz.lomasz.spatialspring.domain.dto.LocationDto;
import xyz.lomasz.spatialspring.repository.LocationRepository;
import xyz.lomasz.spatialspring.service.LocationService;

import java.util.List;
import java.util.Optional;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;
    @Autowired
    private LocationRepository locationRepository;

    @RequestMapping(value ="/location", method = RequestMethod.POST)
    public ResponseEntity addLocation(@RequestBody LocationDto locationDto) {
        if (locationService.findLocationById(locationDto.getId()).isPresent()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Long id = locationService.saveLocation(locationDto);

        UriComponentsBuilder ucBuilder = UriComponentsBuilder.newInstance();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/location/{id}").buildAndExpand(id).toUri());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/location/{id}", method = RequestMethod.GET)
    public ResponseEntity findLocationById(@PathVariable("id") Long id) {
        Optional<LocationDto> location = locationService.findLocationById(id);
        return location.map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/location/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteLocation(@PathVariable("id") Long id) {
        Optional<LocationDto> locationDto = locationService.findLocationById(id);
        if (!locationDto.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        locationRepository.delete(id);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/locations", method = RequestMethod.GET)
    public ResponseEntity<List<LocationDto>> findAllLocations() {
        List<LocationDto> locationDtoList = locationService.findAllLocations();
        return new ResponseEntity<>(locationDtoList, HttpStatus.OK);
    }


}
