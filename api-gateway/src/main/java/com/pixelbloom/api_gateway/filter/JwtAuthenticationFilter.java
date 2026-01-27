package com.pixelbloom.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final String SECRET = "myVeryLongSecretKeyThatIsAtLeast32CharactersLongForJWTSecurityPurposes123456789";

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Step 1: Extract JWT token from Authorization header
            String token = extractToken(exchange);

            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                // Step 2: Parse and validate JWT token
                Claims claims = extractAllClaims(token);
                String userEmail = claims.getSubject();
                String userRole = claims.get("role", String.class);
                Long customerId = claims.get("customerId", Long.class);

                // Step 3: Add user info to request headers for downstream services
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Name", userEmail)
                        .header("X-User-Role", userRole)
                        .header("X-Customer-Id", customerId.toString())
                        .build();

                String path = exchange.getRequest().getPath().toString();

                // Step 4: Role-based access check
                if (path.startsWith("/api/auth/admin/") && !"ADMIN".equals(userRole)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();  // <-- immediately stop here
                }

                // Step 5: Continue with modified request for all allowed requests
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                // Step 6: Return 401 if token is invalid/expired
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    /**
     * Extract Bearer token from Authorization header
     */
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    /**
     * Parse JWT token and extract claims
     */
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static class Config {
        // Empty config class - no configuration needed
    }
}
