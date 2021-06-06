package org.example.controller.dto;

import lombok.Data;

@Data
public class EmailRequestBodyDto {
    private String message;
    private String emailId;
}
