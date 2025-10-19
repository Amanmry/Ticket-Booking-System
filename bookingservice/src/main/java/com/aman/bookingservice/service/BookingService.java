package com.aman.bookingservice.service;

import com.aman.bookingservice.client.InventoryServiceClient;
import com.aman.bookingservice.entity.Customer;
import com.aman.bookingservice.event.BookingEvent;
import com.aman.bookingservice.repository.CustomerRepository;
import com.aman.bookingservice.request.BookingRequest;
import com.aman.bookingservice.response.BookingResponse;
import com.aman.bookingservice.response.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public BookingService(CustomerRepository customerRepository, InventoryServiceClient inventoryServiceClient, KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public BookingResponse createBooking(BookingRequest request) {
        //  Check if user exists
        final Customer customer = customerRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("user not found!"));
        //  Check if there is enough inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
        log.info("Inventory Response : {}", inventoryResponse);
        if(inventoryResponse.getCapacity() < request.getTicketCount()) {
            throw new RuntimeException("Not enough inventory");
        }
        //  Get Event information to also get Venue Information
        //  Create Booking
        final BookingEvent bookingEvent = createBookingEvent(request, customer, inventoryResponse);
        //  Send Booking to Order Service on a Kafka Topic
        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking sent to Kafka: {}", bookingEvent);
        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

    private BookingEvent createBookingEvent(BookingRequest request,
                                            Customer customer,
                                            InventoryResponse inventoryResponse) {
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(request.getEventId())
                .ticketCount(request.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(request.getTicketCount())))
                .build();
    }
}
