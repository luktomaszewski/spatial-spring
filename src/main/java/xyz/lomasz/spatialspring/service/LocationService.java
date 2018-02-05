package xyz.lomasz.spatialspring.service;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import xyz.lomasz.spatialspring.domain.dto.LocationDto;
import xyz.lomasz.spatialspring.domain.dto.LocationWithIdDto;
import xyz.lomasz.spatialspring.domain.entity.LocationEntity;
import xyz.lomasz.spatialspring.domain.mapper.LocationMapper;
import xyz.lomasz.spatialspring.repository.LocationRepository;

@Service
@CommonsLog
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationMapper locationMapper;

    public Optional<LocationWithIdDto> findLocationById(Long id) {
        LocationEntity locationEntity = locationRepository.findById(id);
        if (locationEntity == null) {
            return Optional.empty();
        }
        return Optional.of(convertEntityToDto(locationEntity));
    }

    public List<LocationWithIdDto> findAllLocations() {
        List<LocationEntity> locationEntityList = locationRepository.findAll();
        return locationEntityList.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public boolean exists(Long id) {
        return locationRepository.exists(id);
    }

    public Long saveLocation(LocationDto locationDto) {
        LocationEntity locationEntity = convertDtoToEntity(locationDto);
        locationRepository.save(locationEntity);
        return locationEntity.getId();
    }

    public void deleteLocation(Long id) {
        locationRepository.delete(id);
    }

    public void updateLocation(Long id, LocationDto locationDto) {
        LocationEntity locationEntity = convertDtoToEntity(locationDto);
        locationEntity.setId(id);
        locationRepository.save(locationEntity);
    }

    public LocationWithIdDto convertEntityToDto(LocationEntity locationEntity) {
        LocationWithIdDto locationDto = locationMapper.to(locationEntity);

        Geometry geometry = locationEntity.getLocation();
        GeoJSONWriter writer = new GeoJSONWriter();
        org.wololo.geojson.Geometry geoJson = writer.write(geometry);
        locationDto.setLocation(geoJson);

        return locationDto;
    }

    public LocationEntity convertDtoToEntity(LocationDto locationDto) {
        LocationEntity locationEntity = locationMapper.to(locationDto);

        org.wololo.geojson.Geometry geoJson = locationDto.getLocation();
        GeoJSONReader reader = new GeoJSONReader();
        Geometry geometry = reader.read(geoJson);
        locationEntity.setLocation(geometry);

        return locationEntity;
    }
}
