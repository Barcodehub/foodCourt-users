package co.com.nelumbo.backpmo.infrastructure.security.cognito;

import co.com.nelumbo.backpmo.application.auth.model.UserInfo;
import co.com.nelumbo.backpmo.application.auth.port.CognitoGateway;
import co.com.nelumbo.backpmo.infrastructure.exception.InfrastructureExceptionCode;
import co.com.nelumbo.backpmo.infrastructure.exception.InternalServerException;
import co.com.nelumbo.backpmo.infrastructure.exception.UnauthorizedException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("!test")
@RequiredArgsConstructor
public class CognitoService implements CognitoGateway {

    @Value("${cognito.userPoolId}")
    private String userPoolId;

    @Value("${cognito.region:us-east-1}")
    private String region;

    @Value("${aws.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey:}")
    private String secretAccessKey;

    private final JwtDecoder cognitoJwtDecoder;

    public UserInfo getUserInfo(String accessToken) {
        try {
            Jwt validatedJwt = cognitoJwtDecoder.decode(accessToken);

            log.info("✅ Token validado correctamente");
            log.debug("Subject: {}", validatedJwt.getSubject());
            log.debug("Email: {}", validatedJwt.getClaimAsString("email"));

            String sub = validatedJwt.getClaimAsString("sub");

            log.debug("userPoolId: {}", userPoolId);
            log.debug("sub: {}", sub);

            AWSCognitoIdentityProvider prov = init();
            AdminGetUserRequest request = new AdminGetUserRequest()
                .withUserPoolId(userPoolId)
                .withUsername(sub);

            AdminGetUserResult result = prov.adminGetUser(request);

            Map<String, String> userAttributes = result.getUserAttributes().stream()
                .collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));

            return UserInfo.builder()
                .id(null)
                .sub(userAttributes.get("sub"))
                .email(userAttributes.get("email"))
                .emailVerified(Boolean.parseBoolean(userAttributes.get("email_verified")))
                .name(userAttributes.get("name"))
                .isSuperAdmin(true)
                .isExternal(false)
                .roleIds(Set.of())
                .permissions(Set.of())
                .accessibleModuleIds(Set.of())
                .build();
        } catch (JwtException e) {
            log.error("JwtException al llamar a Cognito", e);
            throw new UnauthorizedException("Invalid or expired Cognito token",
                InfrastructureExceptionCode.UNAUTHORIZED_ACCESS_COGNITO.getCode());
        } catch (NotAuthorizedException e) {
            log.error("Error no autorizado al llamar a Cognito", e);
            throw new UnauthorizedException("Unauthorized access to Cognito",
                InfrastructureExceptionCode.UNAUTHORIZED_ACCESS_COGNITO.getCode());
        } catch (Exception e) {
            log.error("Error inesperado al llamar a Cognito", e);
            throw new InternalServerException("Unexpected error calling Cognito",
                InfrastructureExceptionCode.UNEXPECTED_ERROR_COGNITO.getCode());
        }
    }


    private AWSCognitoIdentityProvider init() {
        AWSCredentialsProvider provider;

        if (accessKeyId != null && !accessKeyId.isBlank()
            && secretAccessKey != null && !secretAccessKey.isBlank()) {
            provider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKeyId, secretAccessKey));
        } else {
            provider = DefaultAWSCredentialsProviderChain.getInstance();
        }

        return AWSCognitoIdentityProviderClientBuilder.standard()
            .withCredentials(provider)
            .withRegion(region)
            .build();
    }
}
