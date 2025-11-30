package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserServicePort {

    UserModel createUser(UserModel userModel);

    Page<UserModel> getAllUsers(Pageable pageable);

    UserModel getUserById(Long id);

}
