package co.com.nelumbo.backpmo.infrastructure.security.annotations;

import co.com.nelumbo.backpmo.domain.security.modules.ModulesEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleVerify {
    String value() default "";

    ModulesEnum enumValue() default ModulesEnum.EMPTY;
}
