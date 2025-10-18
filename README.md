# Ticket Booking System (Spring Boot Microservices)

This project is my hands on attempt to learn and explore the key concepts of microservices architecture using Spring Boot for the backend. It's not a full application but focuses on understanding how it all works.

## High Level Diagram 

![alt text](markdown-images/image.png)

This project will help me get hands on with important microservices ideas like Spring Security, API Gateway, and Circuit Breaker. I’ll also apply key technologies such as Spring Boot 3, Kafka, Docker, and others listed in the table to better understand how everything fits together.

| Concepts           |  Technologies                        |
|------------------- |------------------------------------- |
| 1. Spring Security |  1. Spring Boot 3                    |
| 2. API Gateway     |  2. Java 21                          |
| 3. DB Migration    |  3. Docker                           |
| 4. REST API        |  4. Kafka                            |
| 5. API DOCS        |  5. MySQL                            |
| 6. Circuit Breaker |  6. JPA                              |
| 7. Kafka Topics    |  7. Flyway                           |
| 8. Consumers       |  8. Swagger                          |
| 9. Producers       |  9. Lombok                           |
|                    |  10. Postman                         |
|                    |  11. Keycloak                        |
|                    |  12. Resilience 4j                   |
|                    |  13. Functional Endpoint Programming |


## List of Prerequisites for Development

1. IntelliJ Idea

2. Java 21

3. Postman

4. Docker Desktop

5. MySql Workbench

---

## Working Overview :

![alt text](markdown-images/image-1.png)

###  Client

* The end-user interacts with the system through the **Client UI**.
* Authentication is handled before any service access.

### API Gateway

* Acts as a single entry point for all client requests.
* Forwards requests to respective services (e.g., Booking, Inventory).
* Handles **authentication** and **routing**.

### Booking Service

* Core service that handles **ticket booking** logic.
* Checks ticket availability by calling the **Inventory Service**.
* Places booking requests onto a **queue** for asynchronous processing.
* Communicates with **MySQL DB** for storing booking-related data.

### Inventory Service

* Manages the **available ticket inventory**.
* Updates inventory after a successful booking.
* Communicates with the **Booking Service** and **MySQL DB**.

### Order Service

* Listens to the **queue** for incoming booking requests.
* Processes and confirms **ticket orders**.
* Persists order data in the **MySQL Database**.

### Database (MySQL)

* Central data store shared among services (decoupled via repositories).
* Stores data for **Bookings**, **Inventory**, and **Orders**.

### Queue

* Decouples the **Booking** and **Order** services.
* Ensures non-blocking, scalable order processing.

### Service Communication Flow

1. **Client** authenticates → sends booking request via **API Gateway**.
2. **Booking Service** receives the request → checks availability via **Inventory Service**.
3. If tickets are available → Booking is queued.
4. **Order Service** processes the queue → creates an order.
5. All updates are persisted in **MySQL**.

---

## Let's Start Creating Microservices

Let's head to spring initializer and start creating our microservices : [Spring Boot Starter](https://start.spring.io/)

### 1. Inventory Microservices

Let's generate a project and open in IntelliJ

![alt text](markdown-images/image-2.png)

Database Schema for Inventory Service

![alt text](markdown-images/image-3.png)

