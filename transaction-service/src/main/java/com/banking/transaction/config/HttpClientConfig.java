
package com.banking.transaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient accountRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8085")
                .build();
    }
}
