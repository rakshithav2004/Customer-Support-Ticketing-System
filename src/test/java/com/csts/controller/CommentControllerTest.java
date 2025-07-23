package com.csts.controller;

import com.csts.dto.CommentRequest;
import com.csts.model.Comment;
import com.csts.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddComment_Success() {
        CommentRequest request = new CommentRequest();
        request.setTicketId("ticket123");
        request.setMessage("Test comment");

        Comment comment = new Comment();
        comment.setMessage("Test comment");
        comment.setCreatedAt(LocalDateTime.now());

        when(commentService.addComment(request, authentication)).thenReturn(comment);

        ResponseEntity<?> response = commentController.addComment(request, authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Comment);
        Comment body = (Comment) response.getBody();
        assertEquals("Test comment", body.getMessage());
    }

    @Test
    public void testAddComment_Failure() {
        CommentRequest request = new CommentRequest();
        request.setTicketId("invalid");
        request.setMessage("Test comment");
        Authentication authentication = mock(Authentication.class);
        when(commentService.addComment(request, authentication))
                .thenThrow(new RuntimeException("Ticket Not Found"));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            commentController.addComment(request, authentication);
        });
        assertTrue(thrown.getMessage().contains("Ticket Not Found"));
    }

    @Test
    public void testGetComment_Success() {
        Comment comment = new Comment();
        comment.setMessage("Test comment");

        when(commentService.getCommentsByTicketId("ticket123", authentication))
                .thenReturn(List.of(comment));

        ResponseEntity<?> response = commentController.getComment("ticket123", authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);
        List<?> list = (List<?>) response.getBody();
        assertFalse(list.isEmpty());
    }

    @Test
    public void testGetComment_Forbidden() {
        when(commentService.getCommentsByTicketId("ticket123", authentication))
                .thenThrow(new RuntimeException("Access denied"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentController.getComment("ticket123", authentication);
        });

        assertTrue(exception.getMessage().contains("Access denied"));
    }
}
