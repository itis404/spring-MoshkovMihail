package com.example.tounip.tounip.space.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSpaceRequest {

    @NotBlank
    @Size(max = 80)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean isPublic;
}