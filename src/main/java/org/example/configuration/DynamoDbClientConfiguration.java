package org.example.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
public class DynamoDbClientConfiguration {

    @Bean("messageDbDynamoClient")
    public DynamoDbAsyncClient dynamoDbClient() {
        return DynamoDbAsyncClient.builder().region(Region.EU_WEST_1).build();
    }
}
