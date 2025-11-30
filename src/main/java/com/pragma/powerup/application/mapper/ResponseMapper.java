package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.PaginationMetaDto;
import com.pragma.powerup.apifirst.model.UserDataResponseDto;
import com.pragma.powerup.apifirst.model.UserResponseDto;
import com.pragma.powerup.apifirst.model.UsersListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Responsable de construir los wrappers de respuesta (data + meta)
 * Separa la lógica de construcción de DTOs del Handler
 * Usa MapStruct para generación automática de código
 */
@Mapper(componentModel = "spring")
public interface ResponseMapper {

    /**
     * Envuelve un UserResponseDto en un wrapper UserDataResponseDto
     */
    @Mapping(target = "data", source = "userResponseDto")
    UserDataResponseDto toUserDataResponse(UserResponseDto userResponseDto);

    /**
     * Envuelve una lista de usuarios con metadata de paginación
     * Usa método default para lógica personalizada de Page
     */
    default UsersListResponseDto toUsersListResponse(List<UserResponseDto> users, Page<?> page) {
        PaginationMetaDto meta = toPaginationMeta(page);

        UsersListResponseDto response = new UsersListResponseDto();
        response.setData(users);
        response.setMeta(meta);
        return response;
    }

    /**
     * Extrae metadata de paginación desde un Page de Spring Data
     */
    default PaginationMetaDto toPaginationMeta(Page<?> page) {
        if (page == null) {
            return null;
        }

        PaginationMetaDto meta = new PaginationMetaDto();
        meta.setPage(page.getNumber());
        meta.setSize(page.getSize());
        meta.setTotalElements((int) page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        return meta;
    }
}

