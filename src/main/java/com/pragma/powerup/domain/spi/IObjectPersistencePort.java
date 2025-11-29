package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.ObjectModel;
import java.util.List;
import java.util.Optional;

public interface IObjectPersistencePort {
    ObjectModel saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();

    Optional<ObjectModel> getObjectById(Long id);

    void deleteObject(Long id);
}
