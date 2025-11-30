package co.com.nelumbo.backpmo.infrastructure.security.annotations;

import co.com.nelumbo.backpmo.domain.security.modules.ModulesEnum;
import co.com.nelumbo.backpmo.domain.security.permissions.PermissionEnum;
import co.com.nelumbo.backpmo.domain.security.sections.SectionEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModulePermissionSectionVerify {
    ModulesEnum module();
    PermissionEnum permission() default PermissionEnum.READ;
    PermissionEnum[] permissions() default {};
    SectionEnum section() default SectionEnum.INFO_GENERAL;
    SectionEnum[] sections() default {};
}