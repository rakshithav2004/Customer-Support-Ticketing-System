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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;

    public Comment addComment(CommentRequest request, Authentication authentication) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(request.getTicketId());
        if (ticketOpt.isEmpty()) {
            throw new RuntimeException("Ticket Not Found");
        }
        Ticket ticket = ticketOpt.get();
        Object principal = authentication.getPrincipal();
        User user;
        if (principal instanceof UserPrincipal) {
            String userId = ((UserPrincipal) principal).getId();
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else if (principal instanceof String) {
            String userId = (String) principal;
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            throw new RuntimeException("Unknown principal type: " + principal.getClass());
        }
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        boolean isOwner = ticket.getCreatedBy().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to add a comment on this ticket.");
        }
        Comment comment = new Comment();
        comment.setTicketId(ticket);
        comment.setCommentedBy(user);
        comment.setMessage(request.getMessage());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTicketId(String ticketId, Authentication authentication) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new RuntimeException("Ticket not found");
        }
        Ticket ticket = ticketOpt.get();
        String userId;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            userId = ((UserPrincipal) principal).getId();
        } else if (principal instanceof String) {
            userId = (String) principal;
        } else {
            throw new RuntimeException("Unknown principal type: " + principal.getClass());
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole().name());
        boolean isOwner = ticket.getCreatedBy().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied. You are not allowed to view these comments.");
        }
        List<Comment> comments = commentRepository.findByTicketId(ticketId);
        return comments;
    }
}

