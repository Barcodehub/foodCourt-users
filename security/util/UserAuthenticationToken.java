package co.com.nelumbo.backpmo.infrastructure.security.util;

import co.com.nelumbo.backpmo.application.auth.model.UserInfo;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final UserInfo principal;

    public UserAuthenticationToken(UserInfo principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }
}
