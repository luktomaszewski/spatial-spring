package xyz.lomasz.spatialspring.domain.entity;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity(name = "location")
public class LocationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "geometry")
    private Geometry geometry;

}