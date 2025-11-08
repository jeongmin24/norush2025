package com.capstone.norush2025.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class ExternalApiConfig {

    // WebClient Bean 등록
    @Bean
    @Qualifier("tmapWebClient")
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
