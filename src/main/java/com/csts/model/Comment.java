package com.csts.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private String id;
    @DBRef
    @NotBlank(message = "TicketId is required")
    private Ticket ticketId;
    @DBRef
    private User commentedBy;
    @NotBlank(message = "Message is required")
    private String message;
    private LocalDateTime createdAt;
}