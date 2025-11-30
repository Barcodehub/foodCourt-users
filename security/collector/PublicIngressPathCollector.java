package co.com.nelumbo.backpmo.infrastructure.security.collector;

import co.com.nelumbo.backpmo.infrastructure.security.annotations.PublicIngress;
import lombok.Getter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@DependsOn("requestMappingHandlerMapping")
public class PublicIngressPathCollector implements SmartInitializingSingleton {

    @Getter
    private final Set<String> publicPaths = new HashSet<>();

    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public PublicIngressPathCollector(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod method = entry.getValue();

            boolean isPublic = method.hasMethodAnnotation(PublicIngress.class)
                || method.getBeanType().isAnnotationPresent(PublicIngress.class);

            Set<String> patterns = new HashSet<>();

            if (info.getPathPatternsCondition() != null) {
                patterns.addAll(info.getPathPatternsCondition().getPatternValues());
            }
            else if (info.getPatternsCondition() != null) {
                patterns.addAll(info.getPatternsCondition().getPatterns());
            }
            else {
                System.out.println("⚠ No se encontraron rutas en método: " + method.getMethod().getName());
                continue;
            }

            if (isPublic) {
                publicPaths.addAll(patterns);
                System.out.println("✔ Ruta pública detectada: " + patterns + " en método " + method.getMethod().getName());
            } else {
                System.out.println("✖ Ruta privada: " + patterns + " en método " + method.getMethod().getName());
            }
        }

        System.out.println(">> Public paths (final): " + publicPaths);
    }
}





