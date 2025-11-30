package co.com.nelumbo.backpmo.infrastructure.security.jwt;

import co.com.nelumbo.backpmo.application.auth.model.UserInfo;
import co.com.nelumbo.backpmo.infrastructure.exception.InfrastructureExceptionCode;
import co.com.nelumbo.backpmo.infrastructure.exception.UnauthorizedException;
import co.com.nelumbo.backpmo.infrastructure.repository.MasterSpringUserRepository;
import co.com.nelumbo.backpmo.infrastructure.security.collector.PublicIngressPathCollector;
import co.com.nelumbo.backpmo.infrastructure.security.util.UserContextProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class JwtTokenValidatorInterceptor implements HandlerInterceptor {

    private final MasterSpringUserRepository userRepository;
    private final UserContextProvider userContextProvider;
    private final ObjectProvider<PublicIngressPathCollector> publicIngressPathCollectorProvider;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String path = request.getRequestURI();
        if (path.equals("/auth/exchange")) return true;

        PublicIngressPathCollector collector = publicIngressPathCollectorProvider.getIfAvailable();
        if (collector != null && collector.getPublicPaths().contains(path)) {
            System.out.println("🔓 Interceptor ignorado para ruta pública: " + path);
            return true;
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String tokenValue = jwt.getTokenValue();
            String email = jwt.getClaimAsString("email");

            UserInfo userInfo = UserInfo.builder()
                .id(Long.parseLong(jwt.getClaimAsString("id")))
                .sub(jwt.getClaimAsString("sub"))
                .email(jwt.getClaimAsString("email"))
                .name(jwt.getClaimAsString("name"))
                .isSuperAdmin(Boolean.TRUE.equals(jwt.getClaimAsBoolean("isSuperAdmin")))
                .isExternal(Boolean.TRUE.equals(jwt.getClaimAsBoolean("isExternal")))
                .roleIds(jwt.getClaimAsStringList("roleIds").stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toSet()))
                .permissions(new HashSet<>(jwt.getClaimAsStringList("permissions")))
                .accessibleModuleIds(
                    ((List<?>) jwt.getClaim("moduleIds")).stream()
                        .map(val -> Long.valueOf(val.toString()))
                        .collect(Collectors.toSet())
                )
                .build();

            userContextProvider.setCurrentUser(userInfo);

            if (email == null || email.isBlank()) {
                throw new UnauthorizedException(
                    InfrastructureExceptionCode.EMAIL_NOT_REGISTERED.getMessage(),
                    InfrastructureExceptionCode.EMAIL_NOT_REGISTERED.getCode());
            }

            var userOpt = userRepository.findByEmailAndDeletedAtIsNull(email);
            if (userOpt.isEmpty() || !tokenValue.equals(userOpt.get().getToken())) {
                throw new UnauthorizedException(
                    InfrastructureExceptionCode.TOKEN_IS_INVALID.getMessage(),
                    InfrastructureExceptionCode.TOKEN_IS_INVALID.getCode());
            }

            return true;
        }

        throw new UnauthorizedException(
            InfrastructureExceptionCode.INVALID_AUTH.getMessage(),
            InfrastructureExceptionCode.INVALID_AUTH.getCode()
        );
    }
}
