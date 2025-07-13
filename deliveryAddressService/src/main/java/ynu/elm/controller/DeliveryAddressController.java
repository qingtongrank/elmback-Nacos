package ynu.elm.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ynu.elm.entity.DeliveryAddress;
import ynu.elm.service.DeliveryAddressService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/DeliveryAddressController")
public class DeliveryAddressController {

    @Resource
    private DeliveryAddressService deliveryAddressService;

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/listAddressByUser")
    public ResponseEntity<List<DeliveryAddress>> listAddressByUser(@RequestParam String userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryAddressCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<DeliveryAddress> list = deliveryAddressService.listAddressByUser(userId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/getAddressBydaId")
    public ResponseEntity<DeliveryAddress> getAddressBydaId(@RequestParam String daId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryAddressCircuitBreaker");
        return circuitBreaker.run(() -> {
            Optional<DeliveryAddress> deliveryAddress = deliveryAddressService.getAdderssBydaId(daId);
            return ResponseEntity.ok(deliveryAddress.get());
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/saveAddress")
    public ResponseEntity<DeliveryAddress> saveAddress(@RequestBody DeliveryAddress address) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryAddressCircuitBreaker");
        return circuitBreaker.run(() -> {
            DeliveryAddress saved = deliveryAddressService.saveAddress(address);
            return ResponseEntity.ok(saved);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @DeleteMapping("/removeAddress")
    public ResponseEntity<Void> removeAddress(@RequestParam String daId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryAddressCircuitBreaker");
        return circuitBreaker.run(() -> {
            deliveryAddressService.removeAddress(daId);
            return ResponseEntity.noContent().build();
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }
}