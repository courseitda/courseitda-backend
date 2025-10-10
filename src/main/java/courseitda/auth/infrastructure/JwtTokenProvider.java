package courseitda.auth.infrastructure;

import static courseitda.auth.domain.AuthRole.GUEST;

import courseitda.auth.domain.AuthRole;
import courseitda.auth.domain.AuthTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements AuthTokenProvider {

    private final SecretKey secretKey;
    @Value("${security.jwt.access-token.validity-in-milliseconds}")
    private long validityInMilliseconds;

    public JwtTokenProvider(@Value("${security.jwt.access-token.secret-key}") final String secretKeyValue) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyValue.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(final String principal, final AuthRole role) {
        final Claims claims = Jwts.claims()
                .subject(principal)
                .build();
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .claim("role", role.name())
                .signWith(secretKey)
                .compact();
    }

    public String getPrincipal(final String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Instant getExpiration(final String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .toInstant();
    }

    public AuthRole getRole(final String token) {
        if (token == null || token.isEmpty()) {
            return GUEST;
        }

        final String role = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

        return AuthRole.from(role);
    }

    public boolean isValidToken(final String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (final MalformedJwtException e) {
            // Header.Payload.Signature 3개 파트로 나누어지지 않은 경우, Base64 디코딩이 불가능한 경우, JSON 형태가 아닌 경우
            return false;
        } catch (final ExpiredJwtException e) {
            // 토큰이 만료된 경우
            return false;
        } catch (final UnsupportedJwtException e) {
            // 지원하지 않는 암호화 방식을 사용한 경우 ("alg" 필드로 검증)
            return false;
        } catch (final SignatureException e) {
            // 서명이 올바르지 않은 경우(올바른 SecretKey로 서명되지 않은 경우)
            return false;
        } catch (final JwtException e) {
            // 기타 예외
            return false;
        }
    }
}
