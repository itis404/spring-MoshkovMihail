package com.example.tounip.tounip.auth.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCommand {

    private String phoneNumber;

    private String password;
}