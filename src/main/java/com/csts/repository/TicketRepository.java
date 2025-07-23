package com.csts.repository;

import com.csts.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TicketRepository extends MongoRepository<Ticket,String> {
    List<Ticket> findByCreatedById(String userId);
}