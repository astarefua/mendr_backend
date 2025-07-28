package com.telemed.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    
      
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    	
    	String path = request.getRequestURI();

    	// ✅ Bypass JWT filter for public endpoints
        if (path.startsWith("/auth") ||
            path.startsWith("/uploads") ||
            path.equals("/api/doctors/search") ||   // match exact path
            path.startsWith("/api/doctors/search")) { // or any params
            filterChain.doFilter(request, response);
            return;
        }
    	
    	
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            String email = jwtUtil.getEmail(token);
            String role = jwtUtil.getRole(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.replace("ROLE_", ""))
) // ✅ properly wrapped
                    );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                System.out.println("Authenticated user: " + email + " with role: " + role);
                

                SecurityContextHolder.getContext().setAuthentication(authToken); // 👈 ESSENTIAL
            }
        }

        filterChain.doFilter(request, response);
    }
}


