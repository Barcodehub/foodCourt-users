package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.ObjectRequestDto;
import com.pragma.powerup.apifirst.model.ObjectResponseDto;
import com.pragma.powerup.domain.model.ObjectModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Mapper de MapStruct para convertir entre DTOs generados automáticamente y modelos de dominio.
 * MapStruct generará automáticamente la implementación de esta interfaz.
 *
 * Los DTOs son generados desde open-api.yaml por OpenAPI Generator.
 */
@Mapper(componentModel = "spring")
public interface IObjectApiMapper {

    /**
     * Convierte un ObjectRequestDto (generado) a ObjectModel (dominio)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ObjectModel toModel(ObjectRequestDto dto);

    /**
     * Convierte un ObjectModel (dominio) a ObjectResponseDto (generado)
     */
    ObjectResponseDto toResponseDto(ObjectModel model);

    /**
     * Convierte una lista de ObjectModel a lista de ObjectResponseDto
     */
    List<ObjectResponseDto> toResponseDtoList(List<ObjectModel> models);

    /**
     * Convierte LocalDateTime a OffsetDateTime para compatibilidad con DTOs generados
     */
    default OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atOffset(ZoneOffset.UTC);
    }
}
