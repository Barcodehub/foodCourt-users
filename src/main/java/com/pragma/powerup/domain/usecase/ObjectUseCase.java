package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IObjectServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.ObjectModel;
import com.pragma.powerup.domain.spi.IObjectPersistencePort;

import java.util.List;

public class ObjectUseCase implements IObjectServicePort {

    private final IObjectPersistencePort objectPersistencePort;

    public ObjectUseCase(IObjectPersistencePort objectPersistencePort) {
        this.objectPersistencePort = objectPersistencePort;
    }

    @Override
    public ObjectModel saveObject(ObjectModel objectModel) {
        return objectPersistencePort.saveObject(objectModel);
    }

    @Override
    public List<ObjectModel> getAllObjects() {
        return objectPersistencePort.getAllObjects();
    }

    @Override
    public ObjectModel getObjectById(Long id) {
        return objectPersistencePort.getObjectById(id)
                .orElseThrow(() -> new DomainException("Object not found with id: " + id));
    }

    @Override
    public void deleteObject(Long id) {
        if (!objectPersistencePort.getObjectById(id).isPresent()) {
            throw new DomainException("Object not found with id: " + id);
        }
        objectPersistencePort.deleteObject(id);
    }
}
