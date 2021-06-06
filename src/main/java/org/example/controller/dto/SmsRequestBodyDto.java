package org.example.controller.dto;

import lombok.Data;

@Data
public class SmsRequestBodyDto {
    private String phoneNumber;
    private String message;
}
