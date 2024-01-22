package socialnetwork.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


// @Component

@RequiredArgsConstructor // create constructor with all final fields
// A class from the Spring framework ensuring that the filter is executed only once per HTTP request.
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    // @Autowired
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    //responsible for extracting, validating, and setting the authentication context based on JWTs in incoming requests.
    @Override
    protected void doFilterInternal(
        
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull  FilterChain filterChain)           //a chain of filters to be applied to a request.
        throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;
            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                logger.warn(authHeader);
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);   // todo extract the userEmail from JWT token;

            //after JwtService
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);

    }
}
