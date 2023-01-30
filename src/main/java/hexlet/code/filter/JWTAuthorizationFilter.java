package hexlet.code.filter;

import hexlet.code.component.JWTHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static hexlet.code.config.security.SecurityConfig.DEFAULT_AUTHORITIES;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer";

    private final RequestMatcher publicUrls;

    private final JWTHelper jwtHelper;

    public JWTAuthorizationFilter(final RequestMatcher pPublicUrls,
                                  final JWTHelper pJwtHelper) {
        this.publicUrls = pPublicUrls;
        this.jwtHelper = pJwtHelper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return publicUrls.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final var authToken = Optional.ofNullable(request.getHeader(AUTHORIZATION))
            .map(header -> header.replaceFirst("^" + BEARER, ""))
            .map(String::trim)
            .map(jwtHelper::verify)
            .map(claims -> claims.get(SPRING_SECURITY_FORM_USERNAME_KEY))
            .map(Object::toString)
            .map(this::buildAuthToken)
            .orElseThrow();

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(final String email) {
        return new UsernamePasswordAuthenticationToken(
            email,
            null,
            DEFAULT_AUTHORITIES
        );
    }
}
