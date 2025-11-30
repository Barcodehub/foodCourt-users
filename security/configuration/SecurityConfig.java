package co.com.nelumbo.backpmo.infrastructure.security.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String myJwtSecret;

    @Value("${cognito.issuer}")
    private String cognitoIssuer;

    /**
     * Security filter chain for /auth/exchange - validates Cognito token
     */
    @Bean
    @Order(0)
    public SecurityFilterChain cognitoSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/auth/exchange"))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public JwtDecoder cognitoJwtDecoder() {
        return JwtDecoders.fromIssuerLocation(cognitoIssuer);
    }

    @Bean
    public JwtDecoder myJwtDecoder() {
        byte[] secretBytes = myJwtSecret.getBytes();
        return NimbusJwtDecoder.withSecretKey(
            new SecretKeySpec(secretBytes, "HmacSHA256")
        ).build();
    }
}
