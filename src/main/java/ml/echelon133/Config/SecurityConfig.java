package ml.echelon133.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        public RequestMatcher jwtRequestMatcher() {
            return new AntPathRequestMatcher("/api/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Disable CSRF, Http Basic auth and anonymous users for endpoints secured with tokens
            http.csrf().disable().httpBasic().disable().anonymous().disable();
            // Use this config only if request matches jwtRequestMatcher
            http
                    .requestMatcher(jwtRequestMatcher())
                    .authorizeRequests()
                    .antMatchers(jwtRequestMatcher().toString())
                    .authenticated()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling();


        }
    }

    @Order(2)
    @Configuration
    public static class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public RequestMatcher basicRequestMatcher() {
            return new AntPathRequestMatcher("/users/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http
                    .requestMatcher(basicRequestMatcher())
                    .antMatcher("/users/register")
                    .anonymous()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/users/token")
                    .authenticated()
                    .and()
                    .httpBasic()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }
}
