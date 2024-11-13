package org.library.thelibraryj.authentication.googleAuth.domain;

import org.library.thelibraryj.infrastructure.exception.GoogleApiNotRespondingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
class GoogleWebClientConfig {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.google_data_fetch_uri}")
    private String googleDataFetchUri;

    @Bean
    public WebClient googleWebClient() {
        return WebClient.builder().baseUrl(googleDataFetchUri)
                .filter(ExchangeFilterFunction.ofResponseProcessor(GoogleWebClientConfig::responseFilter))
                .build();
    }

    private static Mono<ClientResponse> responseFilter(ClientResponse response) {
        if(response.statusCode().isError()) return response.bodyToMono(String.class).flatMap(body -> Mono.error(new GoogleApiNotRespondingException(body)));
        return Mono.just(response);
    }
}
