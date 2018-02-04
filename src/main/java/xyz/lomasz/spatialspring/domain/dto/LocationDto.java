package xyz.lomasz.spatialspring.domain.dto;

import lombok.Data;
import org.wololo.geojson.Geometry;

@Data
public class LocationDto {

    private Long id;
    private String name;
    private Geometry location;

}