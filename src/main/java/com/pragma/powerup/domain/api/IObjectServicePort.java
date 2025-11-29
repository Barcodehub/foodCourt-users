package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.ObjectModel;

import java.util.List;

public interface IObjectServicePort {

    ObjectModel saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();

    ObjectModel getObjectById(Long id);

    void deleteObject(Long id);
}
