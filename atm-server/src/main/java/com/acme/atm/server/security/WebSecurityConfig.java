package com.acme.atm.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


import java.util.ArrayList;

@Configuration 
@EnableWebSecurity 
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
    private Environment environment;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		http
			.authorizeRequests()
				.antMatchers("/api/**").authenticated()  // Require authentication for APIs
				.anyRequest().permitAll()
				.and()
			.addFilterBefore(new TomcatLoginFilter(), UsernamePasswordAuthenticationFilter.class)
		    .csrf().disable();
	}

	@Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
             User.withDefaultPasswordEncoder()
                .username("admin")
                .password("takeoff")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

}
