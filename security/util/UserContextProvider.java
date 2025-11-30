package co.com.nelumbo.backpmo.infrastructure.security.util;

import co.com.nelumbo.backpmo.application.auth.model.UserInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContextProvider {
    public UserInfo getCurrentUser() {
        return (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void setCurrentUser(UserInfo userInfo) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthenticationToken(userInfo));
    }
}
