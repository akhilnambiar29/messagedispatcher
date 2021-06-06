package org.example.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class MessageDbRepository {

    private final String tableName;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public MessageDbRepository(@Qualifier("messageDbDynamoClient") final DynamoDbAsyncClient dynamoDbAsyncClient,
                               @Value("${messages.dynamodb.table.name}") final String tableName) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
        this.tableName = tableName;
    }

    public CompletableFuture<PutItemResponse> createSmsMessageInDb(final String userId,
                                                                   final String messageId,
                                                                   final String message,
                                                                   final String phoneNumber) {

        var requestBodyMap = new HashMap<String, AttributeValue>();
        requestBodyMap.put("userId", AttributeValue.builder().s(userId).build());
        requestBodyMap.put("messageId", AttributeValue.builder().s(messageId).build());
        requestBodyMap.put("message", AttributeValue.builder().s(message).build());
        requestBodyMap.put("phoneNumber", AttributeValue.builder().s(phoneNumber).build());

        return dynamoDbAsyncClient.putItem(PutItemRequest.builder().tableName(tableName).item(requestBodyMap).build());

    }

    public CompletableFuture<PutItemResponse> createEmailessageInDb(final String userId,
                                                                    final String messageId,
                                                                    final String message,
                                                                    final String emailId) {
        var requestBodyMap = new HashMap<String, AttributeValue>();
        requestBodyMap.put("userId", AttributeValue.builder().s(userId).build());
        requestBodyMap.put("messageId", AttributeValue.builder().s(messageId).build());
        requestBodyMap.put("message", AttributeValue.builder().s(message).build());
        requestBodyMap.put("emailId", AttributeValue.builder().s(emailId).build());

        return dynamoDbAsyncClient.putItem(PutItemRequest.builder().tableName(tableName).item(requestBodyMap).build());
    }

    public CompletableFuture<GetItemResponse> retrieveMessageById(final String messageId) {

        return dynamoDbAsyncClient.getItem(GetItemRequest.builder().tableName(tableName)
                .key(Map.of("messageId", AttributeValue.builder().s(messageId).build()))
                .build());

    }

    public CompletableFuture<QueryResponse> retrieveAllMessages(final String userId) {


        return dynamoDbAsyncClient.query(QueryRequest.builder().tableName(tableName)
                .indexName("userid_index")
                .keyConditionExpression("userId = :userId")
                .expressionAttributeValues(Map.of(":userId",
                        AttributeValue.builder().s(userId).build()))
                .build());
    }
}
