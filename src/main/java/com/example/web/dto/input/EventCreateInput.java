package com.example.web.dto.input;

import com.example.domain.enums.EventType;
import jakarta.validation.constraints.NotNull;

public class EventCreateInput extends EventInput {

    @NotNull(message = "Event type required")
    public EventType eventType;

}
