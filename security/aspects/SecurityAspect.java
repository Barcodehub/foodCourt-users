package co.com.nelumbo.backpmo.infrastructure.security.aspects;

import co.com.nelumbo.backpmo.infrastructure.security.annotations.*;
import co.com.nelumbo.backpmo.infrastructure.security.validation.SecurityValidator;
import co.com.nelumbo.backpmo.domain.security.modules.ModulesEnum;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final SecurityValidator securityValidator;

    @Before("@annotation(publicIngress) || @within(publicIngress)")
    public void publicAccess(PublicIngress publicIngress) {
        // No validation needed
    }

    @Before("@annotation(externalIngress) || @within(externalIngress)")
    public void externalAccess(ExternalIngress externalIngress) {
        securityValidator.validateExternal();
    }

    @Before("@annotation(superAIngress) || @within(superAIngress)")
    public void superAdminAccess(SuperAIngress superAIngress) {
        securityValidator.validateSuperAdmin();
    }

    @Before("@annotation(co.com.nelumbo.backpmo.infrastructure.security.annotations.ModuleVerify)")
    public void moduleAccess(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        ModuleVerify moduleVerify = method.getAnnotation(ModuleVerify.class);

        if (moduleVerify == null) {
            throw new IllegalStateException("No se encontró la anotación @ModuleVerify en el método.");
        }

        ModulesEnum module = moduleVerify.enumValue() != ModulesEnum.EMPTY
            ? moduleVerify.enumValue()
            : ModulesEnum.fromValue(moduleVerify.value());

        securityValidator.validateModuleAccess(module);
    }

    @Before("@annotation(co.com.nelumbo.backpmo.infrastructure.security.annotations.PermissionVerify)")
    public void permissionAccess(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        PermissionVerify permissionVerify = method.getAnnotation(PermissionVerify.class);

        if (permissionVerify == null) {
            throw new IllegalStateException("PermissionVerify annotation is missing on method");
        }

        securityValidator.validatePermission(permissionVerify.value().getKey());
    }

    @Before("@annotation(co.com.nelumbo.backpmo.infrastructure.security.annotations.ModulePermissionVerify)")
    public void modulePermissionAccess(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ModulePermissionVerify annotation = method.getAnnotation(ModulePermissionVerify.class);

        if (annotation == null) {
            throw new IllegalStateException("ModulePermissionVerify annotation is missing on method");
        }

        securityValidator.validateModulePermission(annotation.module(), annotation.permission().getKey());
    }

    @Before("@annotation(co.com.nelumbo.backpmo.infrastructure.security.annotations.ModulePermissionSectionVerify)")
    public void modulePermissionSectionAccess(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ModulePermissionSectionVerify annotation = method.getAnnotation(ModulePermissionSectionVerify.class);

        if (annotation == null) {
            throw new IllegalStateException("ModulePermissionSectionVerify annotation is missing on method");
        }

        boolean hasMultiplePermissions = annotation.permissions().length > 0;
        boolean hasMultipleSections = annotation.sections().length > 0;

        if (hasMultiplePermissions && hasMultipleSections) {
            // Múltiples permisos Y múltiples secciones
            String[] permissionKeys = new String[annotation.permissions().length];
            String[] sectionKeys = new String[annotation.sections().length];
            for (int i = 0; i < annotation.permissions().length; i++) {
                permissionKeys[i] = annotation.permissions()[i].getKey();
            }
            for (int i = 0; i < annotation.sections().length; i++) {
                sectionKeys[i] = annotation.sections()[i].getKey();
            }
            securityValidator.validateModulePermissionsSections(annotation.module(), permissionKeys, sectionKeys);
        } else if (hasMultiplePermissions) {
            // Múltiples permisos, sección única
            String[] permissionKeys = new String[annotation.permissions().length];
            for (int i = 0; i < annotation.permissions().length; i++) {
                permissionKeys[i] = annotation.permissions()[i].getKey();
            }
            securityValidator.validateModulePermissionsSection(annotation.module(), permissionKeys, annotation.section().getKey());
        } else if (hasMultipleSections) {
            // Permiso único, múltiples secciones
            String[] sectionKeys = new String[annotation.sections().length];
            for (int i = 0; i < annotation.sections().length; i++) {
                sectionKeys[i] = annotation.sections()[i].getKey();
            }
            securityValidator.validateModulePermissionSections(annotation.module(), annotation.permission().getKey(), sectionKeys);
        } else {
            // Permiso único, sección única (compatibilidad)
            securityValidator.validateModulePermissionSection(annotation.module(), annotation.permission().getKey(), annotation.section().getKey());
        }
    }
}
