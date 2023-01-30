package hexlet.code.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

@Component
public class JWTHelper {

    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;
    private final Clock clock;
    private final String secretKey;

    public JWTHelper(@Value("${jwt.issuer:task_manager}") final String pIssuer,
                     @Value("${jwt.expiration-sec:86400}") final Long pExpirationSec,
                     @Value("${jwt.clock-skew-sec:300}") final Long pClockSkewSec,
                     @Value("${jwt.secret:secret}") final String pSecretKey) {
        this.issuer = pIssuer;
        this.expirationSec = pExpirationSec;
        this.clockSkewSec = pClockSkewSec;
        this.clock = DefaultClock.INSTANCE;
        this.secretKey = BASE64.encode(pSecretKey);
    }

    public String expiring(final Map<String, Object> attributes) {
        return Jwts.builder()
            .signWith(HS256, secretKey)
            .setClaims(getClaims(attributes, expirationSec))
            .compact();
    }

    public Map<String, Object> verify(final String token) {
        return Jwts.parser()
            .requireIssuer(issuer)
            .setClock(clock)
            .setAllowedClockSkewSeconds(clockSkewSec)
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
    }

    private Claims getClaims(final Map<String, Object> attributes, final Long expiresInSec) {
        final Claims claims = Jwts.claims();
        claims.setIssuer(issuer);
        claims.setIssuedAt(clock.now());
        claims.putAll(attributes);
        if (expiresInSec > 0) {
            claims.setExpiration(new Date(System.currentTimeMillis() + expiresInSec * 1000));
        }
        return claims;
    }
}
