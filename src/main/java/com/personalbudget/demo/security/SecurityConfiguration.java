package com.personalbudget.demo.security;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    
    @Autowired
    DataSource dataSource;
    
    
    
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception 
    {
        UserBuilder users = User.withDefaultPasswordEncoder();
        
        auth.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("select username,password,enabled from users where username=?")
                .authoritiesByUsernameQuery("select username,authority from authorities where username=?");
    }
        
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        String[] staticResources = {
            "/css/**",
            "/img/**",
            "/signup",
            "/confirm"
        };
        
        http.authorizeRequests()
                .antMatchers(staticResources).permitAll()       // whitelisting URLs to static resources so e.g. login page can access images and styles
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/loginPage")
                .loginProcessingUrl("/authenticateUser")
                .permitAll()
            .and()
                .logout()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();
    }
    
    @Autowired
    public PasswordEncoder passwordEncoder()
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
        
}
