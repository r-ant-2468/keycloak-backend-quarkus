package com.example.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class EventTicketInput {

    @NotNull(message = "Event date required")
    public Instant eventDate;

    @NotBlank(message = "Ticket name required")
    @Size(max = 64, message = "Ticket name cannot exceed 64 characters")
    public String ticketName;

    @Size(max = 2048, message = "Decisions cannot exceed 2048 characters")
    public String decisions;

    @Size(max = 2048, message = "Prognostics cannot exceed 2048 characters")
    public String prognostics;

}
