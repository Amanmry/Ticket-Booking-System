package com.aman.bookingservice.service;

import com.aman.bookingservice.client.InventoryServiceClient;
import com.aman.bookingservice.entity.Customer;
import com.aman.bookingservice.repository.CustomerRepository;
import com.aman.bookingservice.request.BookingRequest;
import com.aman.bookingservice.response.BookingResponse;
import com.aman.bookingservice.response.InventoryResponse;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;

    public BookingService(CustomerRepository customerRepository, InventoryServiceClient inventoryServiceClient) {
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public BookingResponse createBooking(BookingRequest request) {
        //  Check if user exists
        final Customer customer = customerRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("user not found!"));
        //  Check if there is enough inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
        System.out.println("Inventory Response : " + inventoryResponse);
        if(inventoryResponse.getCapacity() < request.getTicketCount()) {
            throw new RuntimeException("Not enough inventory");
        }
        //  Get Event information to also get Venue Information
        //  Create Booking
        //  Send Booking to Order Service on a Kafka Topic

        return BookingResponse.builder().build();
    }
}
