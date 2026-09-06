package br.com.orderflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ViaCepConfig {

    private static final String VIA_CEP_BASE_URL = "https://viacep.com.br/ws";

    @Bean
    public RestClient viaCepRestClient() {
        return RestClient.builder()
                .baseUrl(VIA_CEP_BASE_URL)
                .build();
    }
}
