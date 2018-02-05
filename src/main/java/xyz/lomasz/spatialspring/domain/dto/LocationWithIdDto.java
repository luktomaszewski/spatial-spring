package xyz.lomasz.spatialspring.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocationWithIdDto extends LocationDto {
    Long id;
}
