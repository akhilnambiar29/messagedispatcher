package org.example.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

import java.util.concurrent.CompletableFuture;

@Component
public class EmailSubscriberService {
    private final String snsClientTopicArn;
    private final SnsAsyncClient snsAsyncClient;

    public EmailSubscriberService(@Qualifier("snsClient") final SnsAsyncClient snsAsyncClient,
                                  @Qualifier("snsClientTopicArn") final String snsClientTopicArn) {
        this.snsAsyncClient = snsAsyncClient;
        this.snsClientTopicArn = snsClientTopicArn;
    }

    public CompletableFuture<SubscribeResponse> subEmail(final String emailId) {

        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("email")
                .endpoint(emailId)
                .returnSubscriptionArn(true)
                .topicArn(snsClientTopicArn)
                .build();

        return snsAsyncClient.subscribe(request);

    }
}
