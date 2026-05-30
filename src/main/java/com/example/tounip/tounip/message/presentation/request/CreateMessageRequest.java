package com.example.tounip.tounip.message.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMessageRequest {

    @NotBlank
    @Size(max = 2000)
    private String content;
}