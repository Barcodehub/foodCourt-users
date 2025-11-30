package co.com.nelumbo.backpmo.infrastructure.security.configuration;

import co.com.nelumbo.backpmo.infrastructure.security.jwt.JwtTokenValidatorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtTokenValidatorInterceptor tokenValidatorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidatorInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/auth/exchange");
    }
}
