package com.example.gateway.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorFilter implements GatewayFilterFactory<AggregatorFilter.Config> {

    private final WebClient webClient = WebClient.builder().build();

    @Setter
    @Getter
    public static class Config {
        private List<String> serviceUrls; // List of service URLs to aggregate
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            List<Mono<String>> responseMonos = config.getServiceUrls().stream()
                    .map(url -> webClient.get()
                            .uri(url)
                            .retrieve()
                            .bodyToMono(String.class))
                    .toList();

            Mono<List<String>> aggregatedResponses = Flux.merge(responseMonos)
                    .collectList();

            return aggregatedResponses.flatMap(responses -> {
                // Aggregate as JSON array
                String aggregatedJson = responses.toString();
                byte[] responseBytes = aggregatedJson.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBytes);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange.getResponse().writeWith(Mono.just(buffer));
            });
        };
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

}
