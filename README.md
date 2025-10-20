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

Let's generate a project and open in IntelliJ for `Inventory Microservice`

![alt text](markdown-images/image-2.png)

Database Schema for Inventory Service

![alt text](markdown-images/image-3.png)

now, as we have created the initial database using docker, now let's create database-tables, using `flyway` it's used in industry. This tool helps to migrate database without using DDL.

[Flyway Official Site](https://www.red-gate.com/products/flyway/community/#maven)

### 2. Booking Microservices

New extended Database Schema

![alt text](markdown-images/image-4.png)

Let's generate a project and open in IntelliJ for `Booking Microservice`

![alt text](markdown-images/image-5.png)

### Added Kafka to Docker Compose

```yml

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 3000

  kafka-broker:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka-broker
    ports:
      - "9092:9092"
      - "29092:29092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT, PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-broker:29092, PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  kafka-ui:
    container_name: kafka-ui
    image: provectuslabs/kafka-ui:latest
    ports:
      - "8084:8080"
    depends_on:
      - kafka-broker
    environment:
      KAFKA_CLUSTERS_NAME: local
      KAFKA_CLUSTERS_BOOTSTRAPSERVERS: kafka-broker:29092
      DYNAMIC_CONFIG_ENABLED: 'true'

  kafka-schema-registry:
    image: confluentinc/cp-schema-registry:7.5.0
    hostname: kafka-schema-registry
    container_name: kafka-schema-registry
    depends_on:
      - kafka-broker
    ports:
      - "8083:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: kafka-schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: 'kafka-broker:29092'
      SCHEMA_REGISTRY_LISTENERS: http://kafka-schema-registry:8081

```

### 3. Order Microservices

Let's generate a project and open in IntelliJ for `Order Microservice`

![alt text](markdown-images/image-6.png)

Order Service is a consumer service, for booking service and it helps to place order and update inventory.

---

## API Gateway

An API Gateway is a server that acts as an entry point for client requests and routes them to backend services. It acts as a reverse proxy, handling authentication, request transformation, rate limiting.

### Benefits & Drawbacks

| Benefits                   | Drawbacks               |
| -------------------------- | ----------------------- |
| Centralised entry point    | Single point of failure |
| Load Balancing and Routing | Increased latency       |
| Security                   | Set up complexity       |
| Caching and Compression    | Maintenance             |
| API Versioning             | Potential bottlenecks   |
| Protocol translation       |                         |

In this project will use `Spring Cloud Gateway` as an API Gateway.

![alt text](markdown-images/image-7.png)

---

## Spring Security

We will be using `KEYCLOAK` for securing our API Endpoints.

need to add some more dependencies in Docker for `keycloak`

docker-compose.yml
```yml

  keycloak-db:
    container_name: keycloak-mysql
    image: mysql:9.4.0 
    volumes:
      - ./volume-data/mysql_keycloak_data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: keycloak
      MYSQL_USER: keycloak
      MYSQL_PASSWORD: password

  keycloak:
    container_name: keycloak
    image: quay.io/keycloak/keycloak:24.0.1
    command: ["start-dev", "--import-realm"]
    environment:
      DB_VENDOR: MYSQL
      DB_ARDR: mysql
      DB_DATABASE: keycloak
      DB_USER: keycloak
      DB_PASSWORD: password
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8091:8080"
    volumes:
      - ./docker/keycloak/realms/:/opt/keycloak/data/import/ 
    depends_on:
      - keycloak-db

```

Need to add this dependency in API Gateway `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

---

## Let's add some Resilience to our Microservices

### Circuit Breaker Pattern

![alt text](markdown-images/image-8.png)

let's add some dependencies to our API Gateway Microservice

`pom.xml`
```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

```

## API Documentation

OpenAPI is Specification of how API Documentation should look like and it Implementation is `Swagger`.

So, will be creating API Documentation for `Booking Service` and `Inventory Service` using `API Gateway` so that the one who would be looking into the documentation can only access this endpoints through `API Gateway`.

`pom.xml` for inventory and booking service
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.13</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
    <version>2.8.13</version>
</dependency>
```

then need to create a OpenApiConfig class to customize the HTML page of API Docs

`Booking Service Class for customization of API Docs HTML Page`
```java
package com.aman.bookingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryServiceApi() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Booking Service API")
                        .description("Booking Service API for Ticket Booking System!")
                        .version("v1.0.0"));
    }
}
```

`Inventory Service Class for customization of API Docs HTML Page`
```java
package com.aman.inventoryservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryServiceApi() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Inventory Service API")
                        .description("Inventory Service API for Ticket Booking System!")
                        .version("v1.0.0"));
    }
}
```

[API Docs for Inventory Service](http://localhost:8080/swagger-ui/index.html#/)

[API Docs for Booking Service](http://localhost:8081/swagger-ui/index.html#/)

Now, we want external People to access these docs from API Gateway, so let's do that in API Gateway Microservice and Allow the HTML Page to be accessed without authentication.

