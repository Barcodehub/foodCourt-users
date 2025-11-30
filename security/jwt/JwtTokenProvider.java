package co.com.nelumbo.backpmo.infrastructure.security.jwt;

import co.com.nelumbo.backpmo.application.auth.model.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.validityMillis}")
    private long validityInMillis;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(UserInfo userInfo) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMillis);

        return Jwts.builder()
            .setSubject(userInfo.getId().toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("id", userInfo.getId())
            .claim("email", userInfo.getEmail())
            .claim("name", userInfo.getName())
            .claim("isSuperAdmin", userInfo.isSuperAdmin())
            .claim("isExternal", userInfo.isExternal())
            .claim("permissions", userInfo.getPermissions())
            .claim("roleIds", userInfo.getRoleIds())
            .claim("moduleIds", userInfo.getAccessibleModuleIds())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}
