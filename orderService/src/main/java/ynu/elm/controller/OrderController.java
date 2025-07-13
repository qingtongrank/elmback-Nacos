package ynu.elm.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ynu.elm.entity.Order;
import ynu.elm.service.OrderService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/OrderController")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/listOrderByUser")
    public ResponseEntity<List<Order>> listOrderByUser(@RequestParam String userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");

        return circuitBreaker.run(()->{
            List<Order> list = orderService.listOrderByUser(userId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/listOrderByOrderId")
    public ResponseEntity<List<Order>> listOrderByOrderId(@RequestParam String orderId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");
        return circuitBreaker.run(()->{
            List<Order> list = orderService.listOrderByOrderId(orderId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/listOrderByBusiness")
    public ResponseEntity<List<Order>> listOrderByBusiness(@RequestParam String businessId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");
        return circuitBreaker.run(()->{
            List<Order> list = orderService.listOrderByBusiness(businessId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/listOrderByDriver")
    public ResponseEntity<List<Order>> listOrderByDriver(@RequestParam String driverId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");
        return circuitBreaker.run(()->{
            List<Order> list = orderService.listOrderByDriver(driverId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/listOrderByState")
    public ResponseEntity<List<Order>> listOrderByState(@RequestParam Integer orderState) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");
        return circuitBreaker.run(()->{
            List<Order> list = orderService.listOrderByState(orderState);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/listOrderByDateBetween")
    public ResponseEntity<List<Order>> listOrderByDateBetween(@RequestParam LocalDateTime start,
                                                               @RequestParam LocalDateTime end) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");
        return circuitBreaker.run(()->{
            List<Order> list = orderService.listOrderByDateBetween(start, end);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });

    }

    @PostMapping("/saveOrder")
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");
        return circuitBreaker.run(()->{
            Order saved = orderService.saveOrder(order);
            return ResponseEntity.ok(saved);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @DeleteMapping("/removeOrder")
    public ResponseEntity<Void> removeOrder(@RequestParam String orderId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderCircuitBreaker");
        return circuitBreaker.run(()->{
            orderService.removeOrder(orderId);
            return ResponseEntity.noContent().build();
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }
}