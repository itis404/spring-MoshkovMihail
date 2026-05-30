package com.example.tounip.tounip.session.application.dto;

import com.example.tounip.tounip.user.application.dto.UserAuthInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshSessionResult {

    private UserAuthInfo user;

    private String refreshToken;
}