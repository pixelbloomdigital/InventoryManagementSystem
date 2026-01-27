package com.pixelbloom.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RoleBasedFilter extends AbstractGatewayFilterFactory<RoleBasedFilter.Config> {

    public RoleBasedFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            String userRole = exchange.getRequest().getHeaders().getFirst("X-User-Role");

            // Check role-based access
            if (path.contains("/api/auth/admin/") && !"ADMIN".equals(userRole)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            if (path.contains("/api/auth/user/") &&
                    !"USER".equals(userRole) && !"ADMIN".equals(userRole)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        private String requiredRole;

        public String getRequiredRole() { return requiredRole; }
        public void setRequiredRole(String requiredRole) { this.requiredRole = requiredRole; }
    }
}
