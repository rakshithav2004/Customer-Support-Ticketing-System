package com.csts.service;

import com.csts.dto.TicketRequest;
import com.csts.dto.TicketUpdateRequest;
import com.csts.model.Ticket;
import com.csts.model.User;
import com.csts.repository.TicketRepository;
import com.csts.repository.UserRepository;
import com.csts.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTicket_success() {
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getId()).thenReturn("user123");

        User user = new User();
        user.setId("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        TicketRequest request = new TicketRequest();
        request.setTitle("Test Title");
        request.setDescription("Test Description");
        request.setCategory("Bug");
        request.setPriority(Ticket.Priority.HIGH);

        Ticket savedTicket = new Ticket();
        savedTicket.setTitle(request.getTitle());
        savedTicket.setDescription(request.getDescription());
        savedTicket.setCategory(request.getCategory());
        savedTicket.setPriority(request.getPriority());
        savedTicket.setCreatedBy(user);
        savedTicket.setStatus(Ticket.Status.OPEN);
        savedTicket.setCreatedAt(LocalDateTime.now());

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        Ticket result = ticketService.createTicket(request, authentication);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals(Ticket.Status.OPEN, result.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void createTicket_userNotFound() {
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getId()).thenReturn("user123");

        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        TicketRequest request = new TicketRequest();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.createTicket(request, authentication);
        });

        assertTrue(exception.getMessage().contains("User Not Found"));
    }

    @Test
    void getMyTickets_success() {
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getId()).thenReturn("user123");

        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketRepository.findByCreatedById("user123")).thenReturn(tickets);

        List<Ticket> result = ticketService.getMyTickets(authentication);

        assertEquals(2, result.size());
    }

    @Test
    void getAllTickets_success() {
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketService.getAllTickets();

        assertEquals(2, result.size());
    }

    @Test
    void updateTicket_success() {
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        ticket.setStatus(Ticket.Status.OPEN);

        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));

        TicketUpdateRequest request = new TicketUpdateRequest();
        request.setStatus("CLOSED");

        User assignedUser = new User();
        assignedUser.setId("user456");
        assignedUser.setRole(User.Role.ADMIN);
        request.setAssignedToUserId("user456");

        when(userRepository.findById("user456")).thenReturn(Optional.of(assignedUser));

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket updatedTicket = ticketService.updateTicket("ticket123", request);

        assertEquals(Ticket.Status.CLOSED, updatedTicket.getStatus());
        assertEquals(assignedUser, updatedTicket.getAssignedTo());
        assertNotNull(updatedTicket.getUpdatedAt());
    }

    @Test
    void updateTicket_ticketNotFound() {
        when(ticketRepository.findById("invalid")).thenReturn(Optional.empty());

        TicketUpdateRequest request = new TicketUpdateRequest();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicket("invalid", request);
        });

        assertTrue(exception.getMessage().contains("Ticket not found"));    }

    @Test
    void deleteTicketById_success() {
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");

        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        doNothing().when(ticketRepository).delete(ticket);

        assertDoesNotThrow(() -> ticketService.deleteTicketById("ticket123"));

        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    void deleteTicketById_ticketNotFound() {
        when(ticketRepository.findById("invalid")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.deleteTicketById("invalid");
        });

        assertTrue(exception.getMessage().contains("Ticket Not found"));
    }

    @Test
    void getTicketsById_success() {
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        User user = new User();
        user.setId("user123");
        ticket.setCreatedBy(user);
        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("user123");
        Ticket result = ticketService.getTicketsById("ticket123", authentication);
        assertEquals("ticket123", result.getId());
    }

    @Test
    void getTicketsById_notFound() {
        when(ticketRepository.findById("invalid")).thenReturn(Optional.empty());
        Authentication authentication = mock(Authentication.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.getTicketsById("invalid", authentication);
        });
        assertTrue(exception.getMessage().contains("Ticket not found"));
    }

}