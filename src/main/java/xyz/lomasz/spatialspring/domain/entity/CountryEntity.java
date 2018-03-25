package xyz.lomasz.spatialspring.domain.entity;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity(name = "country")
public class CountryEntity {

    @Id
    @Column(name = "id")
    @NotNull
    private Long id;

    @NotNull
    @Column(name = "shape")
    private Geometry geometry;

    @Column(name = "iso_a2", length = 2)
    private String iso2;

    @Column(name = "iso_a3", length = 3)
    private String iso3;

    @Column(name = "name", length = 40)
    private String name;

}