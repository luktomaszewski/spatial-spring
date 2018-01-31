package xyz.lomasz.spatialspring.repository;

import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import xyz.lomasz.spatialspring.model.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {

    List<Location> findAll();
    Optional<Location> findById(Long id);
    List<Location> findByLocation(Geometry location);

}