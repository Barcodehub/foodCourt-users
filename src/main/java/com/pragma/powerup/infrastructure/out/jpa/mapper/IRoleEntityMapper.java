package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface IRoleEntityMapper {

    RoleModel toDomain(RoleEntity roleEntity);

    RoleEntity toEntity(RoleModel roleModel);

}
