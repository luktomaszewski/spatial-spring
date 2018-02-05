package xyz.lomasz.spatialspring.repository;

import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.lomasz.spatialspring.domain.entity.LocationEntity;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    List<LocationEntity> findAll();
    LocationEntity findById(Long id);

    @Query("SELECT l FROM location AS l WHERE within(l.geometry, :filter) = TRUE")
    List<LocationEntity> findWithin(@Param("filter") Geometry filter);

}