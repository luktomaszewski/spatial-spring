package xyz.lomasz.spatialspring.repository;

import com.vividsolutions.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.lomasz.spatialspring.domain.entity.LocationEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    boolean existsLocationEntityByUserAndId(String user, Long id);

    List<LocationEntity> findAllByUser(String user);

    Optional<LocationEntity> findByUserAndId(String user, Long id);

    @Query("SELECT l FROM location AS l WHERE l.id.user = :user AND within(l.geometry, :filter) = TRUE")
    List<LocationEntity> findWithin(@Param("user") String user, @Param("filter") Geometry filter);

}