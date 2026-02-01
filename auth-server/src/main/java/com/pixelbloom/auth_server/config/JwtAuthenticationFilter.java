package com.pixelbloom.auth_server.config;

import com.pixelbloom.auth_server.enums.CustomerStatus;
import com.pixelbloom.auth_server.model.Customer;
import com.pixelbloom.auth_server.repository.CustomerRepository;
import com.pixelbloom.auth_server.service.CustomerService;
import com.pixelbloom.auth_server.service.CustomerUserDetailsService;
import com.pixelbloom.auth_server.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomerRepository customerRepository;
    @Autowired
    @Lazy
    private CustomerUserDetailsService customerUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomerRepository customerRepository) {
        this.jwtUtil = jwtUtil;
        this.customerRepository = customerRepository;
    }
      @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String jwtRole = jwtUtil.extractRole(jwt); // "ADMIN" from payload
            UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + jwtRole));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // MUST
            }
        }
        filterChain.doFilter(request, response);
    }


}



