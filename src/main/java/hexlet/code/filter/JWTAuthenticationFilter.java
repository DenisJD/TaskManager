package hexlet.code.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LoginDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JWTHelper jwtHelper;

    public JWTAuthenticationFilter(final AuthenticationManager authenticationManager,
                                   final RequestMatcher loginRequest,
                                   final JWTHelper pJwtHelper) {
        super(authenticationManager);
        super.setRequiresAuthenticationRequestMatcher(loginRequest);
        this.jwtHelper = pJwtHelper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        final LoginDto loginData = getLoginData(request);
        final var authRequest = new UsernamePasswordAuthenticationToken(
            loginData.getEmail(),
            loginData.getPassword()
        );
        setDetails(request, authRequest);
        return getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        final UserDetails user = (UserDetails) authResult.getPrincipal();
        final String token = jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, user.getUsername()));
        response.getWriter().print(token);
    }

    private LoginDto getLoginData(final HttpServletRequest request) throws AuthenticationException {
        try {
            final String json = request.getReader()
                .lines()
                .collect(Collectors.joining());
            Map<String, String> mapJson = MAPPER.readValue(json, new TypeReference<>() {
            });

            return new LoginDto(mapJson.get("email"), mapJson.get("password"));
        } catch (IOException exception) {
            throw new BadCredentialsException("Can't extract login data from request");
        }
    }
}
