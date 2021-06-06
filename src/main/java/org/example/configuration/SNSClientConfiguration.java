package org.example.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

import java.util.concurrent.ExecutionException;

@Configuration
public class SNSClientConfiguration {


    @Bean("snsClient")
    public SnsAsyncClient snsClient() {
        return SnsAsyncClient.builder().region(Region.EU_WEST_1).build();
    }

    @Bean("snsClientTopicArn")
    public String snsClientTopicArn(@Qualifier("snsClient") final SnsAsyncClient asyncClient,
                                    @Value("${messages.topic.name}") final String topicName) throws ExecutionException, InterruptedException {
        return asyncClient.createTopic(CreateTopicRequest.builder().name(topicName).build())
                .thenApply(CreateTopicResponse::topicArn).get();
    }

}
