package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface IUserEntityMapper {

    @Mapping(target = "createdBy", expression = "java(mapUserIdSimple(entity.getCreatedBy()))")
    UserModel toDomain(UserEntity entity);
    UserEntity toEntity(UserModel role);


    default UserModel mapUserIdSimple(UserEntity userEntity) {
        if (userEntity == null) return null;
        UserModel u = new UserModel();
        u.setId(userEntity.getId());
        return u;
    }
}
