package br.com.orderflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public S3Client createS3Instance() {
        final var provider = DefaultCredentialsProvider.builder().build();

        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(provider)
                .build();
    }
}
