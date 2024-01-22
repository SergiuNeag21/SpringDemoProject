package socialnetwork.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import socialnetwork.domain.Role;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(csrf -> csrf.disable()).authorizeHttpRequests(  //Cross-Site Request Forgery) protection is a security feature that helps prevent attackers from executing malicious actions on behalf of an authenticated user
                    requests -> requests
                    .requestMatchers("/api/v1/auth/*")
                    .permitAll()   // For anything else, require authentication.
                    .requestMatchers("/api/users/getAllUsers").hasAuthority(Role.ADMIN.toString())
                    .anyRequest().authenticated()
                    )
                    
                .sessionManagement(management -> management          //This configuration sets the session management behavior.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)                     // This interface is responsible for authenticating users based on their credentials
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);   //indicates that JWT authentication should be processed before the default username-password authentication.

        return httpSecurity.build();
    }
}
