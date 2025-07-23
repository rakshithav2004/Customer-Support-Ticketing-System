package com.csts.service;


import com.csts.dto.CommentRequest;
import com.csts.exception.AccessDeniedException;
import com.csts.model.Comment;
import com.csts.model.Ticket;
import com.csts.model.User;
import com.csts.repository.CommentRepository;
import com.csts.repository.TicketRepository;
import com.csts.repository.UserRepository;
import com.csts.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
        void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddComment_Success() {
        CommentRequest request = new CommentRequest();
        request.setTicketId("ticket123");
        request.setMessage("Hello comment");
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        User ticketOwner = new User();
        ticketOwner.setId("user123");
        ticket.setCreatedBy(ticketOwner);
        User user = new User();
        user.setId("user123");
        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getId()).thenReturn("user123");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(principal).getAuthorities();
        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        Comment savedComment = new Comment();
        savedComment.setMessage("Hello comment");
        savedComment.setTicketId(ticket);
        savedComment.setCommentedBy(user);
        savedComment.setCreatedAt(LocalDateTime.now());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        Comment result = commentService.addComment(request, auth);
        assertNotNull(result);
        assertEquals("Hello comment", result.getMessage());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testGetCommentsByTicketId_AdminAccess() {
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        User creator = new User();
        creator.setId("user456");
        ticket.setCreatedBy(creator);
        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(principal).getAuthorities();
        when(principal.getId()).thenReturn("admin123");
        when(auth.getPrincipal()).thenReturn(principal);
        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        User adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setRole(User.Role.ADMIN);
        when(userRepository.findById("admin123")).thenReturn(Optional.of(adminUser));
        when(commentRepository.findByTicketId("ticket123")).thenReturn(List.of(new Comment()));
        List<Comment> comments = commentService.getCommentsByTicketId("ticket123", auth);
        assertNotNull(comments);
        assertFalse(comments.isEmpty());
        verify(commentRepository, times(1)).findByTicketId("ticket123");
    }


    @Test
    void testGetCommentsByTicketId_AccessDenied() {
        User creator = new User();
        creator.setId("user123");
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        ticket.setCreatedBy(creator);
        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        doReturn(authorities).when(principal).getAuthorities();
        when(principal.getId()).thenReturn("user999");
        when(auth.getPrincipal()).thenReturn(principal);
        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        User user999 = new User();
        user999.setId("user999");
        user999.setRole(User.Role.CUSTOMER);
        when(userRepository.findById("user999")).thenReturn(Optional.of(user999));
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            commentService.getCommentsByTicketId("ticket123", auth);
        });
        assertTrue(exception.getMessage().contains("Access denied"));
        verify(commentRepository, never()).findByTicketId(any());
    }
}