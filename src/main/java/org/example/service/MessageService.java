package org.example.service;

import lombok.AllArgsConstructor;
import org.example.controller.dto.CreateResponseDto;
import org.example.exception.MessageNotFoundException;
import org.example.repository.MessageDbRepository;
import org.example.repository.MessageRepository;
import org.example.repository.dto.MessageDto;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageDbRepository messageDbRepository;
    private final EmailSubscriberService emailSubscriberService;

    public CompletableFuture<CreateResponseDto> createSmsMessage(final String userId,
                                                                 final String message,
                                                                 final String phoneNumber) {
        return messageRepository.createSmsMessage(message, phoneNumber)
                .thenCompose(messageId -> messageDbRepository
                        .createSmsMessageInDb(userId, messageId, message, phoneNumber)
                        .thenApply(unused -> new CreateResponseDto(messageId)));
    }

    public CompletableFuture<CreateResponseDto> createEmailessage(final String userId,
                                                                  final String message,
                                                                  final String emailId) {
        return emailSubscriberService.subEmail(emailId)
                .thenCompose(subscribeResponse -> messageRepository.createEmailessage(message))
                .thenCompose(messageId -> messageDbRepository
                        .createEmailessageInDb(userId, messageId, message, emailId)
                        .thenApply(unused -> new CreateResponseDto(messageId)));
    }

    public CompletableFuture<MessageDto> retrieveMessageById(final String userId,
                                                             final String messageId) {
        return messageDbRepository.retrieveMessageById(messageId)
                .thenApply(getItemResponse -> {
                    if (getItemResponse.hasItem()) {
                        MessageDto messageDto = getMessageFromMap(getItemResponse.item());
                        if (messageDto.getUserId().equals(userId)) {
                            return messageDto;
                        }
                        throw new MessageNotFoundException();
                    }
                    throw new MessageNotFoundException();
                });
    }

    public CompletableFuture<List<MessageDto>> retrieveAllMessages(final String userId) {
        return messageDbRepository.retrieveAllMessages(userId)
                .thenApply(queryResponse -> queryResponse
                        .items().stream()
                        .map(this::getMessageFromMap)
                        .collect(Collectors.toList()));
    }

    private MessageDto getMessageFromMap(final Map<String, AttributeValue> item) {

        return MessageDto.builder()
                .messageId(item.get("messageId").s())
                .message(item.get("message").s())
                .emailId(item.get("email").s())
                .phoneNumber(item.get("phoneNumber").s())
                .build();
    }
}
