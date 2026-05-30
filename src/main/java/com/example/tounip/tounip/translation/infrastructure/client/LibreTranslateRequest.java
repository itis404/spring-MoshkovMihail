package com.example.tounip.tounip.translation.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LibreTranslateRequest {

    private String q;

    private String source;

    private String target;

    private String format;

    private String apiKey;
}