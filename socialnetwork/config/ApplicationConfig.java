package socialnetwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import socialnetwork.repository.UserRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByEmail(username)
        .orElseThrow(()-> new UsernameNotFoundException("User not Found!"));
    }

    //This method encapsulates a DaoAuthenticationProvider configured with a custom UserDetailsService and a PasswordEncoder.
    // This authentication provider bean is likely used later in the Spring Security configuration to handle user authentication.
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtService jwtService(){
        JwtService service = null;
        System.out.println("\n\n\n\n  AICICICIICICII!!!!!!!! \n\n\n\n\n\n");
        log.error("\n\n\n\n  AICICICIICICII!!!!!!!! \n\n\n\n\n\n");
        try {
            service = new JwtService();
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return service;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService){
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}
