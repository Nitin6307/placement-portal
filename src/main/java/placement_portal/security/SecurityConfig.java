package placement_portal.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .exceptionHandling(exception -> exception

                        // 401 - Not authenticated
                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            "{\"error\":\"Authentication required or token is invalid\"}"
                                    );
                                }
                        )

                        // 403 - Not authorized
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            "{\"error\":\"Access denied\"}"
                                    );
                                }
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        .requestMatchers("/api/students/me/**")
                        .hasRole("STUDENT")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/students"
                        )
                        .hasRole("STUDENT")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/students"
                        )
                        .hasRole("OFFICER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/students/*"
                        )
                        .hasRole("OFFICER")
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers("/api/students/me/resume").hasRole("STUDENT")
                        .requestMatchers("/api/students/resume/**").authenticated()

                        .requestMatchers("/api/companies/**")
                        .hasRole("OFFICER")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/**")
                        .hasAnyRole("STUDENT", "OFFICER")

                        .requestMatchers("/api/jobs/**")
                        .hasRole("OFFICER")

                        .requestMatchers("/api/applications/*/status")
                        .hasRole("OFFICER")

                        .requestMatchers("/api/applications/**")
                        .hasAnyRole("STUDENT", "OFFICER")

                        .requestMatchers("/api/eligibility/**")
                        .hasRole("STUDENT")

                        .requestMatchers("/api/skill-gap/**")
                        .hasRole("STUDENT")

                        .requestMatchers("/api/recommendations/**")
                        .hasRole("STUDENT")

                        .requestMatchers("/api/skills/**")
                        .hasRole("OFFICER")

                        .requestMatchers("/api/users/**")
                        .hasRole("OFFICER")

                        .requestMatchers("/api/analytics/**")
                        .hasRole("OFFICER")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}