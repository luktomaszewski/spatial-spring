package xyz.lomasz.spatialspring.domain.entity;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity(name = "location")
public class LocationEntity {

    @Id
    @Column(name = "id")
    @NotNull
    @GeneratedValue
    private Long id;

    @Column(name = "user_id", length = 30)
    private String user;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "geometry")
    private Geometry geometry;

}