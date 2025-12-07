package com.pragma.powerup.domain.spi;


import com.pragma.powerup.domain.model.RoleModel;

import java.util.Optional;

public interface IRolePersistencePort {
    Optional<RoleModel> findById(Long id);
}
