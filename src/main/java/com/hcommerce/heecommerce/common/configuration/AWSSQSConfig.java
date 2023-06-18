package com.hcommerce.heecommerce.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.config.SqsListenerConfigurer;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class AWSSQSConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretAccessKey;

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String sqsUrl;

    @Profile("!test")
    @Bean
    @Primary
    public SqsAsyncClient sqsAsync() {
        return SqsAsyncClient.builder()
            .endpointOverride(URI.create(sqsUrl))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider
                .create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
            .build();
    }

    @Profile("!test")
    @Bean
    @Primary
    SqsListenerConfigurer sqsListenerConfigurer(ObjectMapper objectMapper) {
        return registrar -> registrar.setObjectMapper(objectMapper);
    }

    @Profile("!test")
    @Bean
    @Primary
    public SqsTemplate sqsTemplate(ObjectMapper objectMapper) {
        return SqsTemplate
            .builder()
            .sqsAsyncClient(sqsAsync())
            .configureDefaultConverter(converter -> {
                converter.setObjectMapper(objectMapper);
                converter.setPayloadTypeHeader("json");
            })
            .build();
    }
}
