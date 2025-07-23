package com.csts.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    private String id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    private String category;
    @NotNull(message = "Priority is required")
    private Priority priority;
    @NotNull(message = "Status is required")
    private Status status= Status.OPEN;
    private LocalDateTime createdAt=LocalDateTime.now();
    @DBRef(lazy = false)
    private User createdBy;
    @DBRef(lazy = false)
    private User assignedTo;
    private LocalDateTime updatedAt;
    public enum Priority{
        LOW,
        MEDIUM,
        HIGH;
    }
    public enum Status{
        OPEN,
        IN_PROGRESS,
        CLOSED
    }
}
