package com.acme.atm.server.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;


public class TomcatLoginFilter extends GenericFilterBean {

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
			
			if( httpRequest.getRequestURI().contains("/api/") ||
				httpRequest.getRequestURI().contains("/auth/"))
			{
				// Proceed with authentication
				if(AuthHandler.isValidUser(httpRequest))
			    {
					String principal = "admin";
			        String roles = "ROLE_ADMIN";
			        
			        // Read the roles sent in the token
			        Collection<? extends GrantedAuthority> authorities =
			            Arrays.asList(roles.split(",")).stream()
				                .map(authority -> new SimpleGrantedAuthority(authority))
				                .collect(Collectors.toList());

			        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
					SecurityContextHolder.getContext().setAuthentication(authentication);
			    }
				else
				{
					((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);					
					return;
				}
			}
			
			// always need to do this
			filterChain.doFilter(servletRequest, servletResponse);
			
		} catch (Exception e) {
			// log.info("Security exception for user {}", eje.getMessage());
			((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
