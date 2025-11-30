package co.com.nelumbo.backpmo.infrastructure.security.jwt;

import co.com.nelumbo.backpmo.application.auth.model.UserInfo;
import co.com.nelumbo.backpmo.application.auth.port.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements TokenGenerator {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String createToken(UserInfo userInfo) {
        return jwtTokenProvider.createToken(userInfo);
    }
}
