package org.example.repository;

import cloud.localstack.ServiceName;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(services = {ServiceName.DYNAMO})
class MessageDbRepositoryTest {

    private static final String userId = UUID.randomUUID().toString();
    private static final String messageId = UUID.randomUUID().toString();
    private static final String message = "Sample message";
    private static final String phoneNumber = "+31998349923";
    private static final String email = "example@email.org";
    private static DynamoDbAsyncClient dynamoDbAsyncClient;
    private MessageDbRepository messageDbRepository;

    @BeforeAll
    public static void setupDynamo() throws URISyntaxException {
        URI uri = new URI("http://localhost:4566");
        dynamoDbAsyncClient =
                DynamoDbAsyncClient.builder().region(Region.EU_WEST_1).endpointOverride(uri).build();

        var keySchemaList =
                List.of(KeySchemaElement.builder().attributeName("messageId").keyType(KeyType.HASH).build());

        var attributeDefinitionList = List.of(
                AttributeDefinition.builder().attributeName("messageId").attributeType(ScalarAttributeType.S).build(), AttributeDefinition.builder().attributeName("userId").attributeType(ScalarAttributeType.S).build());

        dynamoDbAsyncClient.createTable(CreateTableRequest.builder().tableName("TableName")
                .keySchema(keySchemaList)
                .attributeDefinitions(attributeDefinitionList)
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(10L).writeCapacityUnits(10L).build())
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName("userid_index")
                        .keySchema(KeySchemaElement.builder().attributeName("userId").keyType(KeyType.HASH).build())
                        .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(10L).writeCapacityUnits(10L).build())
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build())
                .build()).join();
    }

    @BeforeEach
    public void setup() {
        messageDbRepository = new MessageDbRepository(dynamoDbAsyncClient, "TableName");
    }

    @Test
    void createSmsMessageInDb() {

        var response = messageDbRepository.createSmsMessageInDb(userId, messageId, message,
                phoneNumber).join();
        Assertions.assertNotNull(response);

    }

    @Test
    void createEmailessageInDb() {
        var response = messageDbRepository.createEmailessageInDb(userId, messageId, message,
                email).join();
        Assertions.assertNotNull(response);
    }

    @Test
    void retrieveMessageById() {
        messageDbRepository.createEmailessageInDb(userId, messageId, message, email).join();
        var response = messageDbRepository.retrieveMessageById(messageId).join();
        Assertions.assertEquals(messageId, response.item().get("messageId").s());
    }

    @Test
    void retrieveAllMessages() {
        messageDbRepository.createEmailessageInDb(userId, messageId, message + "1", email).join();
        messageDbRepository.createSmsMessageInDb(userId, messageId + "2", message, phoneNumber).join();
        var response = messageDbRepository.retrieveAllMessages(userId).join().items();
        Assertions.assertEquals(2, response.size());
    }
}