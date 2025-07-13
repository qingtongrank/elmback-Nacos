package ynu.elm.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ynu.elm.entity.OrderDetailet;
import ynu.elm.service.OrderDetailetService;

import java.util.List;

@RestController
@RequestMapping("/OrderDetailetController")
public class OrderDetailetController {

    @Resource
    private OrderDetailetService orderDetailetService;

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/listDetailetByOrder")
    public ResponseEntity<List<OrderDetailet>> listDetailetByOrder(@RequestParam String orderId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderDetailetCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<OrderDetailet> list = orderDetailetService.listDetailetByOrder(orderId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/saveDetailet")
    public ResponseEntity<OrderDetailet> saveDetailet(@RequestBody OrderDetailet detailet) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderDetailetCircuitBreaker");
        return circuitBreaker.run(() -> {
            OrderDetailet saved = orderDetailetService.saveDetailet(detailet);
            return ResponseEntity.ok(saved);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @DeleteMapping("/removeDetailet")
    public ResponseEntity<Void> removeDetailet(@RequestParam String odId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("orderDetailetCircuitBreaker");
        return circuitBreaker.run(() -> {
            orderDetailetService.removeDetailet(odId);
            return ResponseEntity.noContent().build();
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }
}