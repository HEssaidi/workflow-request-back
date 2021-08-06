package ma.neoxia.workflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/h2-console/**"
                );
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
                .csrf()
                .disable()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .antMatcher("api/request/restricted")  //just the restricted url that needs authentication
                .authorizeRequests()
                .antMatchers(new String[]{"api/request/","/camunda/app/welcome/default/#!/login", "api/request/tasks/","api/request/email/","api/request/remark/", "api/files/file"}).permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login();


    }



}
