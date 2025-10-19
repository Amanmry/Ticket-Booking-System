package com.aman.inventoryservice.service;

import com.aman.inventoryservice.entity.Event;
import com.aman.inventoryservice.entity.Venue;
import com.aman.inventoryservice.repository.EventRepository;
import com.aman.inventoryservice.repository.VenueRepository;
import com.aman.inventoryservice.response.EventInventoryResponse;
import com.aman.inventoryservice.response.VenueInventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public InventoryService(EventRepository eventRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
    }

    public List<EventInventoryResponse> getAllEvents() {
        final List<Event> events = eventRepository.findAll();
        return events.stream().map(event -> EventInventoryResponse.builder()
                .event(event.getName())
                .capacity(event.getLeftCapacity())
                .venue(event.getVenue())
                .ticketPrice(event.getTicketPrice())
                .eventId(event.getId())
                .build()).collect(Collectors.toList());
    }

    public VenueInventoryResponse getVenueInformation(Long venueId) {
        final Venue venue = venueRepository.findById(venueId).orElseThrow(() -> new RuntimeException("no venue found by id : " + venueId));
        return VenueInventoryResponse.builder()
                .venueId(venue.getId())
                .venueName(venue.getName())
                .totalCapacity(venue.getTotalCapacity())
                .build();
    }

    public EventInventoryResponse getEventInventory(Long eventId) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("no event found by id : " + eventId));
        return EventInventoryResponse.builder()
                .event(event.getName())
                .capacity(event.getLeftCapacity())
                .venue(event.getVenue())
                .ticketPrice(event.getTicketPrice())
                .eventId(event.getId())
                .build();
    }

    public void updateEventCapacity(Long eventId, Long ticketsBooked) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("no event found by id : " + eventId));
        event.setLeftCapacity(event.getLeftCapacity() - ticketsBooked);
        eventRepository.saveAndFlush(event);
        log.info("Updated event capacity for event id: {} with tickets booked: {}", eventId, ticketsBooked);
    }
}
