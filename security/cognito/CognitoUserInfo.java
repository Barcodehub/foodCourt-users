package co.com.nelumbo.backpmo.infrastructure.security.cognito;

import lombok.Data;

@Data
public class CognitoUserInfo {
    private String sub;
    private boolean emailVerified;
    private String email;
}
