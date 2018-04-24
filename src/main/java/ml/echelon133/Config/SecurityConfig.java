package ml.echelon133.Config;

import ml.echelon133.Security.JWTAuthenticationFilter;
import ml.echelon133.Security.JWTAuthenticationManager;
import ml.echelon133.Security.JWTAuthenticationProvider;
import ml.echelon133.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Order(1)
    @Configuration
    public static class JWTSecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public AuthenticationProvider jwtAuthenticationProvider() {
            return new JWTAuthenticationProvider();
        }
        @Bean
        public AuthenticationManager jwtAuthenticationManager() {
            return new JWTAuthenticationManager(jwtAuthenticationProvider());
        }

        @Bean
        public RequestMatcher jwtRequestMatcher() {
            return new AntPathRequestMatcher("/api/**");
        }

        @Bean
        public JWTAuthenticationFilter jwtAuthenticationFilter() {
            JWTAuthenticationFilter filter = new JWTAuthenticationFilter();
            filter.setAuthenticationManager(jwtAuthenticationManager());
            filter.setRequiresAuthenticationRequestMatcher(jwtRequestMatcher());
            filter.setContinueChainBeforeSuccessfulAuthentication(true);
            return filter;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .anonymous().disable()
                    .httpBasic().disable()
                    .requestMatcher(jwtRequestMatcher())
                        .authorizeRequests()
                            .antMatchers("/api/**").authenticated()
                        .and()
                            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                            .exceptionHandling()
                        .and()
                            .addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);
        }
    }

    @Order(2)
    @Configuration
    public static class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public RequestMatcher basicRequestMatcher() {
            return new AntPathRequestMatcher("/users/**");
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new CustomUserDetailsService();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .requestMatcher(basicRequestMatcher())
                        .authorizeRequests()
                            .antMatchers("/users/token").authenticated()
                            .antMatchers("/users/register").anonymous()
                        .and()
                            .httpBasic()
                        .and()
                            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                            .exceptionHandling();
        }
    }
}
