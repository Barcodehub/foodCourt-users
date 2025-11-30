package co.com.nelumbo.backpmo.infrastructure.security.validation;


import co.com.nelumbo.backpmo.domain.security.modules.ModulesEnum;

public interface SecurityValidator {
    void validateSuperAdmin();
    void validateExternal();
    void validateModuleAccess(ModulesEnum module);
    void validatePermission(String permissionKey);
    void validateModulePermission(ModulesEnum module, String permissionKey);
    void validateModulePermissionSection(ModulesEnum module, String permissionKey, String sectionKey);
    void validateModulePermissionSections(ModulesEnum module, String permissionKey, String[] sectionKeys);
    void validateModulePermissionsSection(ModulesEnum module, String[] permissionKeys, String sectionKey);
    void validateModulePermissionsSections(ModulesEnum module, String[] permissionKeys, String[] sectionKeys);
}
