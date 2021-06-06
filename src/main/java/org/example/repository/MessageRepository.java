package org.example.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.concurrent.CompletableFuture;

@Component
public class MessageRepository {

    private final SnsAsyncClient snsClient;
    private final String snsClientTopicArn;

    public MessageRepository(@Qualifier("snsClient") final SnsAsyncClient snsClient,
                             @Qualifier("snsClientTopicArn") final String snsClientTopicArn) {
        this.snsClient = snsClient;
        this.snsClientTopicArn = snsClientTopicArn;
    }

    public CompletableFuture<String> createSmsMessage(final String message,
                                                      final String phoneNumber) {
        return snsClient.publish(PublishRequest.builder().message(message).phoneNumber(phoneNumber).build())
                .thenApply(PublishResponse::messageId);
    }

    public CompletableFuture<String> createEmailessage(final String message) {
        return snsClient.publish(PublishRequest.builder().message(message).topicArn(snsClientTopicArn).build())
                .thenApply(PublishResponse::messageId);
    }
}
