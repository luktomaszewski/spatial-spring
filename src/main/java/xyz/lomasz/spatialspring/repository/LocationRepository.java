package xyz.lomasz.spatialspring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import xyz.lomasz.spatialspring.domain.entity.LocationEntity;

import java.util.List;

@Repository
public interface LocationRepository extends CrudRepository<LocationEntity, Long> {

    List<LocationEntity> findAll();
    LocationEntity findById(Long id);

}