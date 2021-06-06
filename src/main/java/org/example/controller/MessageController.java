package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.controller.dto.CreateResponseDto;
import org.example.controller.dto.EmailRequestBodyDto;
import org.example.controller.dto.SmsRequestBodyDto;
import org.example.repository.dto.MessageDto;
import org.example.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/sms")
    public CompletableFuture<CreateResponseDto> createSmsMessage(final @RequestBody SmsRequestBodyDto smsRequestBodyDto,
                                                                 final @RequestAttribute(
                                                                         "COGNITO_IDENTITY_ATTRIBUTE") String userId) {

        return messageService.createSmsMessage(userId, smsRequestBodyDto.getMessage(),
                smsRequestBodyDto.getPhoneNumber());
    }

    @PostMapping("/email")
    public CompletableFuture<CreateResponseDto> createEmailessage(final @RequestBody EmailRequestBodyDto emailRequestBodyDto,
                                                                  final @RequestAttribute(
                                                                          "COGNITO_IDENTITY_ATTRIBUTE") String userId) {
        return messageService.createEmailessage(userId, emailRequestBodyDto.getMessage(),
                emailRequestBodyDto.getEmailId());

    }

    @GetMapping("/{messageId}")
    public CompletableFuture<MessageDto> retrieveMessageById(final @PathVariable("messageId") String messageId,
                                                             final @RequestAttribute(
                                                                     "COGNITO_IDENTITY_ATTRIBUTE") String userId) {
        return messageService.retrieveMessageById(userId, messageId);
    }

    @GetMapping()
    public CompletableFuture<List<MessageDto>> retrieveAllMessages(
            final @RequestAttribute("COGNITO_IDENTITY_ATTRIBUTE") String userId) {
        return messageService.retrieveAllMessages(userId);
    }

}
