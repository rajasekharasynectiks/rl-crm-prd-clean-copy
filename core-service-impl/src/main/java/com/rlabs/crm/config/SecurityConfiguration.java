package com.rlabs.crm.config;


import com.rlabs.crm.security.AuthoritiesConstants;
import com.rlabs.crm.security.jwt.JwtBearerTokenAccessDeniedExceptionEntryPoint;
import com.rlabs.crm.security.jwt.JwtBearerTokenAuthExceptionEntryPoint;
import com.rlabs.crm.security.jwt.JwtBearerTokenAuthenticationFilter;
import com.rlabs.crm.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity
//@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;
//    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
//        this.problemSupport = problemSupport;
    }

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtBearerTokenAuthExceptionEntryPoint jwtBearerTokenAuthExceptionEntryPoint;

    @Autowired
    private JwtBearerTokenAccessDeniedExceptionEntryPoint jwtBearerTokenAccessDeniedExceptionEntryPoint;



    @Bean
    public JwtBearerTokenAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtBearerTokenAuthenticationFilter();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
//        authProvider.setPreAuthenticationChecks(new AccountStatusUserDetailsChecker());
        return authProvider;
    }

    @Autowired
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                authz ->
                    // prettier-ignore
                    authz
                        .requestMatchers(mvc.pattern("/auth/sign-in")).permitAll()
                        .requestMatchers(mvc.pattern("/auth/sign-in/forgot-password-otp")).permitAll()
                        .requestMatchers(mvc.pattern("/auth/sign-in/reset-password")).permitAll()
                        .requestMatchers(mvc.pattern("/auth/sign-in/otp")).permitAll()
                        .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(mvc.pattern("/api/**")).authenticated()
                        .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(mvc.pattern("/management/health")).permitAll()
                        .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                        .requestMatchers(mvc.pattern("/management/info")).permitAll()
                        .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                        .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(
                exceptions ->
                    exceptions
                        .authenticationEntryPoint(jwtBearerTokenAuthExceptionEntryPoint)
                        .accessDeniedHandler(jwtBearerTokenAccessDeniedExceptionEntryPoint)
            )
            .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
