package co.com.nelumbo.backpmo.infrastructure.security.configuration;

import co.com.nelumbo.backpmo.infrastructure.security.collector.PublicIngressPathCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class InternalApiSecurityConfig {

    private final PublicIngressPathCollector pathCollector;
    private final JwtDecoder myJwtDecoder;

    @Bean
    @Order(1)
    public SecurityFilterChain publicEndpoints(HttpSecurity http) throws Exception {
        Set<String> publicPaths = pathCollector.getPublicPaths();

        return http
            .securityMatcher(request -> publicPaths.stream().anyMatch(p -> request.getRequestURI().equals(p)))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain protectedEndpoints(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(myJwtDecoder)))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}



