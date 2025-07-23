package com.csts.service;
import com.csts.security.UserPrincipal;
import com.csts.dto.TicketRequest;
import com.csts.dto.TicketUpdateRequest;
import com.csts.model.Ticket;
import com.csts.model.User;
import com.csts.repository.TicketRepository;
import com.csts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public Ticket createTicket(TicketRequest request, Authentication authentication){
        try{
            String userId;
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                userId = (String) principal;
            } else if (principal instanceof UserPrincipal) {
                userId = ((UserPrincipal) principal).getId();
            } else {
                throw new RuntimeException("Invalid principal type");
            }
            Optional<User> user = userRepository.findById(userId);
            if(user.isEmpty()){
                throw new RuntimeException("User Not Found");
            }
            User user1 = user.get();
            Ticket ticket = new Ticket();
            ticket.setTitle(request.getTitle());
            ticket.setDescription(request.getDescription());
            ticket.setCategory(request.getCategory());
            ticket.setPriority(request.getPriority());
            ticket.setStatus(Ticket.Status.OPEN);
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setCreatedBy(user1);
            return ticketRepository.save(ticket);
        }catch (Exception e){
            throw new RuntimeException("Error creating Ticket: " + e.getMessage());
        }
    }

    public List<Ticket> getMyTickets(Authentication authentication) {
        String userId;
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            userId = (String) principal;
        } else if (principal instanceof UserPrincipal) {
            userId = ((UserPrincipal) principal).getId();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        return ticketRepository.findByCreatedById(userId);
    }

    public List<Ticket> getAllTickets(){
        try {
            return ticketRepository.findAll();
        }catch (Exception e){
            throw  new RuntimeException("Error Fetching all Tickets: " + e.getMessage());
        }
    }
    public Ticket updateTicket(String ticketId, TicketUpdateRequest request){
        try {
            Optional<Ticket> ticket = ticketRepository.findById(ticketId);
            if(ticket.isEmpty()){
                throw new RuntimeException("Ticket not found");
            }
            Optional<User> assignedToUser = userRepository.findById(request.getAssignedToUserId());
            if(assignedToUser.isEmpty()){
                throw new RuntimeException("Assigned User not found");
            }
            User assignedUser = assignedToUser.get();
            if (assignedUser.getRole() != User.Role.ADMIN) {
                throw new RuntimeException("Only ADMIN users can be assigned tickets.");
            }
            Ticket.Status statusEnum = Ticket.Status.valueOf(request.getStatus());
            Ticket ticket1 = ticket.get();
            ticket1.setStatus(statusEnum);
            ticket1.setAssignedTo(assignedUser);
            ticket1.setUpdatedAt(LocalDateTime.now());
            return ticketRepository.save(ticket1);
        } catch (Exception e) {
            throw new RuntimeException("Error Updating Tickets: " + e.getMessage());
        }
    }

    public void deleteTicketById(String ticketId) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket Not found"));
            ticketRepository.delete(ticket);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete ticket: " + e.getMessage());
        }
    }
    public Ticket getTicketsById(String ticketId, Authentication authentication) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Object principal = authentication.getPrincipal();
        String userId;
        if (principal instanceof String) {
            userId = (String) principal;
        } else if (principal instanceof UserPrincipal) {
            userId = ((UserPrincipal) principal).getId();
        } else {
            throw new RuntimeException("Invalid principal type");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin || ticket.getCreatedBy().getId().equals(userId)) {
            return ticket;
        } else {
            throw new RuntimeException("Access denied: Not authorized to view this ticket");
        }
    }
}