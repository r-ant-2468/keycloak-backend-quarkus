package com.example.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

abstract class EventInput {

    @NotBlank(message = "Event title required")
    @Size(max = 128, message = "Title cannot exceed 128 characters")
    public String title;

    @Size(max = 1024, message = "Description cannot exceed 1024 characters")
    public String description;

}
