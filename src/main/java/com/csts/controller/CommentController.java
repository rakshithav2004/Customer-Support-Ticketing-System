package com.csts.controller;

import com.csts.dto.CommentRequest;
import com.csts.exception.AccessDeniedException;
import com.csts.model.Comment;
import com.csts.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Comments", description = "Endpoints for adding and retrieving comments related to tickets")
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @Operation(summary = "Add a comment to a ticket", description = "Allows a CUSTOMER or ADMIN to add a comment.")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest request, Authentication authentication) {
        Comment comment = commentService.addComment(request, authentication);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Get comments by ticket ID", description = "Retrieves all comments associated with a specific ticket.")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<?> getComment(@PathVariable String ticketId, Authentication authentication) {
        try {
            List<Comment> comments = commentService.getCommentsByTicketId(ticketId, authentication);
            return ResponseEntity.ok(comments);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}