package com.aman.orderservice.service;

import com.aman.bookingservice.event.BookingEvent;
import com.aman.orderservice.client.InventoryServiceClient;
import com.aman.orderservice.entity.Order;
import com.aman.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryServiceClient;

    public OrderService(OrderRepository orderRepository, InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @KafkaListener(topics = "booking", groupId = "order-service")
    public void orderEvent(BookingEvent bookingEvent) {
        log.info("Received order event: {}", bookingEvent);

        // Create Order Object for Database
        Order order = createOrder(bookingEvent);
        orderRepository.saveAndFlush(order);

        // Update Inventory
        // Create InventoryClient
        inventoryServiceClient.updateInventory(order.getEventId(), order.getTicketCount());
        log.info("Inventory updated for event: {}, less tickets: {}", order.getEventId(), order.getTicketCount());
    }

    private Order createOrder(BookingEvent bookingEvent) {
        return Order.builder()
                .customerId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

}
