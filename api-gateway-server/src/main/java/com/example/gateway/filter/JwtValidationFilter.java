package com.example.gateway.filter;

import com.example.gateway.Exception.JwtException;
import com.example.gateway.security.JwsParser;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements GatewayFilterFactory<JwtValidationFilter.Config> {

    private final JwsParser jwsParser;

    @Setter
    @Getter
    public static class Config {
        private String tokenHeaderName;
        private Map<String, String> claimToHeaderMap; // key: claim name, value: header name
        private List<String> removeHeaders; // headers to remove from the request
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            final String token = this.getTokenFromHeader(request, config);
            if (token == null || token.isEmpty()) {
                throw new JwtException("Token is missing in the request headers");
            }
            log.info("Token: {}", token);
            log.info("Custom Filter Executed: {}", config.getTokenHeaderName());

            try {
                // Parse the token
                JWTClaimsSet signedJWT = jwsParser.parse(token);
                // Optional: You can extract claims here and add headers if needed
                ServerHttpRequest.Builder mutatedRequest = request.mutate();
                if (config.getClaimToHeaderMap() != null) {
                    for (Map.Entry<String, String> entry : config.getClaimToHeaderMap().entrySet()) {
                        String claimValue = signedJWT.getStringClaim(entry.getKey());
                        if (claimValue != null) {
                            mutatedRequest.header(entry.getValue(), claimValue);
                        }
                    }
                }

                config.removeHeaders.
                        forEach(header -> mutatedRequest.headers(headers -> headers.remove(header)));

                return chain.filter(exchange.mutate().request(mutatedRequest.build()).build());
            } catch (Exception e) {
                throw new JwtException("Error processing JWT: " + e.getMessage(), e);
            }

        };
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    private String getTokenFromHeader(ServerHttpRequest request, Config config) {
        return request.getHeaders().getFirst(config.getTokenHeaderName());
    }

}