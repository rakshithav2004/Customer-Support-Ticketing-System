package com.csts.controller;

import com.csts.dto.TicketRequest;
import com.csts.dto.TicketUpdateRequest;
import com.csts.model.Ticket;
import com.csts.service.TicketService;
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

@Tag(name = "Ticket Management", description = "Endpoints for creating, viewing, updating, and deleting support tickets")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    @Operation(
            summary = "Create a new ticket (CUSTOMER only)",
            description = "Allows a CUSTOMER to create a support ticket."
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketRequest request, Authentication authentication){
        try{
            Ticket createdTicket=ticketService.createTicket(request,authentication);
            return ResponseEntity.ok(createdTicket);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "View own tickets (CUSTOMER only)",
            description = "Allows a CUSTOMER to view tickets they have submitted."
    )
    @GetMapping("/my-tickets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyTickets(Authentication authentication){
        try{
            List<Ticket> tickets =ticketService.getMyTickets(authentication);
            return ResponseEntity.ok(tickets);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "View all tickets (ADMIN only)",
            description = "Allows an ADMIN to retrieve all support tickets in the system."
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTickets(){
        try{
            return ResponseEntity.ok(ticketService.getAllTickets());
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Update ticket by ID (ADMIN only)",
            description = "Allows an ADMIN to update any ticket using its ID."
    )
    @PutMapping("/update/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTicket(@PathVariable String ticketId,@Valid @RequestBody TicketUpdateRequest request){
        try{
            Ticket ticket = ticketService.updateTicket(ticketId,request);
            return ResponseEntity.ok(ticket);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "View ticket by ID (CUSTOMER or ADMIN)",
            description = "Allows a CUSTOMER to view their own ticket or an ADMIN to view any ticket."
    )
    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> getTicketsById(@PathVariable String ticketId, Authentication authentication) {
        try {
            Ticket ticket = ticketService.getTicketsById(ticketId, authentication);
            return new ResponseEntity<>(ticket, HttpStatus.OK);   // Return the single ticket here!
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Delete ticket by ID (ADMIN only)",
            description = "Allows an ADMIN to delete a ticket from the system."
    )
    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTicket(@PathVariable String ticketId) {
        try {
            ticketService.deleteTicketById(ticketId);
            return ResponseEntity.ok("Ticket Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}