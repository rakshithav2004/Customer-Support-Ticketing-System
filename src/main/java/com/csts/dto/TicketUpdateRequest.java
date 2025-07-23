package com.csts.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketUpdateRequest {
    @NotNull(message = "Status is required")
    private String status;
    @NotNull(message = "ADMIN ID to assign ticket is required ")
    private String assignedToUserId;
}