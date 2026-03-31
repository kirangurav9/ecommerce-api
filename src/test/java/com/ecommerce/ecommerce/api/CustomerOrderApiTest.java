package com.ecommerce.ecommerce.api;

import com.ecommerce.ecommerce.dto.CustomerDTO;
import com.ecommerce.ecommerce.dto.OrderDTO;
import com.ecommerce.ecommerce.entity.Customer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerOrderApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String base(String path) {
        return "http://localhost:" + port + path;
    }

    // ---- Customer API ----

    @Test
    @Order(1)
    void createCustomer_returnsCreatedCustomer() {
        Customer customer = new Customer();
        customer.setName("Alice Smith");
        customer.setEmail("alice@example.com");
        customer.setPhone("0871234567");
        customer.setAddress("123 Main St");

        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity(
                base("/customers"), customer, CustomerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Alice Smith");
        assertThat(response.getBody().getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    @Order(2)
    void getCustomer_returnsCustomer_afterCreation() {
        // Create customer first
        Customer customer = new Customer();
        customer.setName("Bob Jones");
        customer.setEmail("bob@example.com");
        CustomerDTO created = restTemplate.postForObject(base("/customers"), customer, CustomerDTO.class);

        // Retrieve it
        ResponseEntity<CustomerDTO> response = restTemplate.getForEntity(
                base("/customers/" + created.getId()), CustomerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Bob Jones");
    }

    @Test
    @Order(3)
    void getCustomer_returns404_whenNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                base("/customers/9999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("9999");
    }

    @Test
    @Order(4)
    void deleteCustomer_removesCustomer() {
        // Create customer
        Customer customer = new Customer();
        customer.setName("Charlie");
        customer.setEmail("charlie@example.com");
        CustomerDTO created = restTemplate.postForObject(base("/customers"), customer, CustomerDTO.class);

        // Delete
        restTemplate.delete(base("/customers/" + created.getId()));

        // Verify gone
        ResponseEntity<String> response = restTemplate.getForEntity(
                base("/customers/" + created.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ---- Order API ----

    @Test
    @Order(5)
    void createOrder_returnsOrder_linkedToCustomer() {
        // Create customer first
        Customer customer = new Customer();
        customer.setName("Diana");
        customer.setEmail("diana@example.com");
        CustomerDTO created = restTemplate.postForObject(base("/customers"), customer, CustomerDTO.class);

        // Create order for that customer
        OrderDTO orderDTO = new OrderDTO(null, LocalDate.of(2024, 6, 15), 200.00, "PENDING");

        ResponseEntity<OrderDTO> response = restTemplate.postForEntity(
                base("/orders/customer/" + created.getId()), orderDTO, OrderDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAmount()).isEqualTo(200.00);
        assertThat(response.getBody().getStatus()).isEqualTo("PENDING");
    }

    @Test
    @Order(6)
    void createOrder_returns404_whenCustomerNotFound() {
        OrderDTO orderDTO = new OrderDTO(null, LocalDate.of(2024, 6, 15), 100.00, "PENDING");

        ResponseEntity<String> response = restTemplate.postForEntity(
                base("/orders/customer/9999"), orderDTO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(7)
    void updateOrder_updatesFieldsCorrectly() {
        // Create customer
        Customer customer = new Customer();
        customer.setName("Eve");
        customer.setEmail("eve@example.com");
        CustomerDTO created = restTemplate.postForObject(base("/customers"), customer, CustomerDTO.class);

        // Create order
        OrderDTO orderDTO = new OrderDTO(null, LocalDate.of(2024, 6, 15), 100.00, "PENDING");
        OrderDTO createdOrder = restTemplate.postForObject(
                base("/orders/customer/" + created.getId()), orderDTO, OrderDTO.class);

        // Update order
        OrderDTO updated = new OrderDTO(null, LocalDate.of(2024, 8, 1), 500.00, "SHIPPED");
        HttpEntity<OrderDTO> request = new HttpEntity<>(updated);
        ResponseEntity<OrderDTO> response = restTemplate.exchange(
                base("/orders/" + createdOrder.getId()), HttpMethod.PUT, request, OrderDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("SHIPPED");
        assertThat(response.getBody().getAmount()).isEqualTo(500.00);
    }

    @Test
    @Order(8)
    void filterOrders_returnsOrdersInDateRange() {
        // Create customer and order
        Customer customer = new Customer();
        customer.setName("Frank");
        customer.setEmail("frank@example.com");
        CustomerDTO created = restTemplate.postForObject(base("/customers"), customer, CustomerDTO.class);

        OrderDTO orderDTO = new OrderDTO(null, LocalDate.of(2024, 6, 15), 75.00, "PENDING");
        restTemplate.postForObject(base("/orders/customer/" + created.getId()), orderDTO, OrderDTO.class);

        // Filter
        ResponseEntity<OrderDTO[]> response = restTemplate.getForEntity(
                base("/orders/filter?start=2024-01-01&end=2024-12-31"), OrderDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}
