package co.com.nelumbo.backpmo.infrastructure.security.validation;

import co.com.nelumbo.backpmo.application.auth.model.UserInfo;
import co.com.nelumbo.backpmo.domain.security.modules.ModulesEnum;
import co.com.nelumbo.backpmo.infrastructure.repository.PermissionJpaRepository;
import co.com.nelumbo.backpmo.infrastructure.security.util.UserContextProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityValidatorImpl implements SecurityValidator {

    private final UserContextProvider userContextProvider;
    private final PermissionJpaRepository permissionRepository;

    @Override
    public void validateSuperAdmin() {
        UserInfo user = userContextProvider.getCurrentUser();
        if (!user.isSuperAdmin()) {
            throw new SecurityException("Requires super admin");
        }
    }

    @Override
    public void validateExternal() {
        UserInfo user = userContextProvider.getCurrentUser();
        if (!user.isExternal()) {
            throw new SecurityException("External access required");
        }
    }

    @Override
    public void validateModuleAccess(ModulesEnum module) {
        UserInfo user = userContextProvider.getCurrentUser();
        if (user.isSuperAdmin()) return;
        if (!permissionRepository.userHasModuleAccess(user.getId(), module.getModuleId())) {
            throw new SecurityException("Access denied to module " + module.name());
        }
    }

    @Override
    public void validatePermission(String permissionKey) {
        UserInfo user = userContextProvider.getCurrentUser();
        if (user.isSuperAdmin()) return;
        if (!permissionRepository.userHasPermission(user.getId(), permissionKey)) {
            throw new SecurityException("Permission '" + permissionKey + "' required");
        }
    }

    @Override
    public void validateModulePermission(ModulesEnum module, String permissionKey){
        log.info("Validating module permission: {} - {}", module.name(), permissionKey);
        UserInfo user = userContextProvider.getCurrentUser();
        if (user.isSuperAdmin()) return;
        if (!permissionRepository.userHasModulePermission(user.getId(), module.getModuleId(), permissionKey)) {
            throw new SecurityException("Permission '" + permissionKey + "' required in module " + module.name());
        }
    }

    @Override
    public void validateModulePermissionSection(ModulesEnum module, String permissionKey, String sectionKey) {
        UserInfo user = userContextProvider.getCurrentUser();
        if (user.isSuperAdmin()) return;
        if (!permissionRepository.userHasModulePermissionSection(user.getId(), module.getModuleId(), permissionKey, sectionKey)) {
            throw new SecurityException("Access denied to section '" + sectionKey + "' in module " + module.name());
        }
    }

    @Override
    public void validateModulePermissionSections(ModulesEnum module, String permissionKey, String[] sectionKeys) {
        UserInfo user = userContextProvider.getCurrentUser();
        if (user.isSuperAdmin()) return;
        for (String sectionKey : sectionKeys) {
            if (permissionRepository.userHasModulePermissionSection(user.getId(), module.getModuleId(), permissionKey, sectionKey)) {
                return; // Usuario tiene acceso a al menos una sección
            }
        }
        throw new SecurityException("Access denied to any of the required sections in module " + module.name());
    }

    @Override
    public void validateModulePermissionsSection(ModulesEnum module, String[] permissionKeys, String sectionKey) {
        UserInfo user = userContextProvider.getCurrentUser();
        if (user.isSuperAdmin()) return;
        for (String permissionKey : permissionKeys) {
            if (permissionRepository.userHasModulePermissionSection(user.getId(), module.getModuleId(), permissionKey, sectionKey)) {
                return; // Usuario tiene al menos uno de los permisos
            }
        }
        throw new SecurityException("Access denied: requires any of the specified permissions in section '" + sectionKey + "' of module " + module.name());
    }

    @Override
    public void validateModulePermissionsSections(ModulesEnum module, String[] permissionKeys, String[] sectionKeys) {
        UserInfo user = userContextProvider.getCurrentUser();
        if (user.isSuperAdmin()) return;
        for (String permissionKey : permissionKeys) {
            for (String sectionKey : sectionKeys) {
                if (permissionRepository.userHasModulePermissionSection(user.getId(), module.getModuleId(), permissionKey, sectionKey)) {
                    return; // Usuario tiene al menos una combinación válida
                }
            }
        }
        throw new SecurityException("Access denied: requires any of the specified permissions in any of the specified sections in module " + module.name());
    }
}
