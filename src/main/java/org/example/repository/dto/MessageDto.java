package org.example.repository.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {
    private String messageId;
    private String phoneNumber;
    private String message;
    private String emailId;
    private String userId;
}
