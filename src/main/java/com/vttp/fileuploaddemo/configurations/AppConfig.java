package com.vttp.fileuploaddemo.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AppConfig {
    @Value("${bucket.secret}")
    private String secret;

    @Value("${bucket.access}")
    private String access;

    @Bean
    public AmazonS3 getS3Client() {
        EndpointConfiguration epConfig = new EndpointConfiguration(
                "sgp1.digitaloceanspaces.com", "sgp1");
        BasicAWSCredentials creds = new BasicAWSCredentials(access, secret);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(epConfig)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }
}
