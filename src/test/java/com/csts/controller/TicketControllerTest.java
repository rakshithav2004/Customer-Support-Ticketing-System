package com.csts.controller;

import com.csts.dto.TicketRequest;
import com.csts.dto.TicketUpdateRequest;
import com.csts.model.Ticket;
import com.csts.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TicketControllerTest {

    @InjectMocks
    private TicketController ticketController;

    @Mock
    private TicketService ticketService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTicket_success() {
        TicketRequest request = new TicketRequest();
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");

        when(ticketService.createTicket(request, authentication)).thenReturn(ticket);

        ResponseEntity<?> response = ticketController.createTicket(request, authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ticket, response.getBody());
    }

    @Test
    void createTicket_error() {
        TicketRequest request = new TicketRequest();
        when(ticketService.createTicket(request, authentication)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = ticketController.createTicket(request, authentication);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void getMyTickets_success() {
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketService.getMyTickets(authentication)).thenReturn(tickets);

        ResponseEntity<?> response = ticketController.getMyTickets(authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tickets, response.getBody());
    }

    @Test
    void getMyTickets_error() {
        when(ticketService.getMyTickets(authentication)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = ticketController.getMyTickets(authentication);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void getAllTickets_success() {
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketService.getAllTickets()).thenReturn(tickets);

        ResponseEntity<?> response = ticketController.getAllTickets();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tickets, response.getBody());
    }

    @Test
    void getAllTickets_error() {
        when(ticketService.getAllTickets()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = ticketController.getAllTickets();
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void updateTicket_success() {
        TicketUpdateRequest request = new TicketUpdateRequest();
        Ticket ticket = new Ticket();

        when(ticketService.updateTicket("ticket123", request)).thenReturn(ticket);

        ResponseEntity<?> response = ticketController.updateTicket("ticket123", request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ticket, response.getBody());
    }

    @Test
    void updateTicket_error() {
        TicketUpdateRequest request = new TicketUpdateRequest();

        when(ticketService.updateTicket("ticket123", request)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = ticketController.updateTicket("ticket123", request);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void getTicketsById_success() {
        Ticket ticket = new Ticket();
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        when(ticketService.getTicketsById("ticket123", authentication)).thenReturn(ticket);
        ResponseEntity<?> response = ticketController.getTicketsById("ticket123", authentication);
        assertEquals(200, response.getStatusCodeValue());
        Ticket responseTicket = (Ticket) response.getBody();
        assertEquals(ticket, responseTicket);
    }



    @Test
    void getTicketsById_forbidden() {
        when(ticketService.getTicketsById("ticket123", authentication))
                .thenThrow(new RuntimeException("Access denied: Not authorized to view this ticket"));

        ResponseEntity<?> response = ticketController.getTicketsById("ticket123", authentication);
        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Access denied"));
    }

    @Test
    void deleteTicket_success() {
        doNothing().when(ticketService).deleteTicketById("ticket123");

        ResponseEntity<?> response = ticketController.deleteTicket("ticket123");
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Deleted Successfully"));
    }

    @Test
    void deleteTicket_error() {
        doThrow(new RuntimeException("Error")).when(ticketService).deleteTicketById("ticket123");

        ResponseEntity<?> response = ticketController.deleteTicket("ticket123");
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error"));
    }
}
