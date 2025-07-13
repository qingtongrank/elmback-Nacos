package ynu.elm.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ynu.elm.entity.Cart;
import ynu.elm.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/CartController")
public class CartController {

    @Resource
    private CartService cartService;

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/listCartByUser")
    public ResponseEntity<List<Cart>> listCartByUser(@RequestParam String userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("cartCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<Cart> list = cartService.listCartByUser(userId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/listCartByUserAndBusiness")
    public ResponseEntity<List<Cart>> listCartByUserAndBusiness(@RequestParam String userId,
                                                                @RequestParam String businessId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("cartCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<Cart> list = cartService.listCartByUserAndBusiness(userId, businessId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/saveCart")
    public ResponseEntity<Cart> saveCart(@RequestBody Cart cart) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("cartCircuitBreaker");
        return circuitBreaker.run(() -> {
            cart.setQuantity(1);
            Cart saved = cartService.saveCart(cart);
            return ResponseEntity.ok(saved);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/updateCart")
    public ResponseEntity<Cart> updateCart(@RequestBody Cart cart) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("cartCircuitBreaker");
        return circuitBreaker.run(() -> {
            Cart updated = cartService.updateCart(cart);
            return ResponseEntity.ok(updated);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/removeCart")
    public ResponseEntity<Integer> removeCart(@RequestParam String userId, @RequestParam String businessId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("cartCircuitBreaker");
        return circuitBreaker.run(() -> {
            Integer row = cartService.removeCart(userId, businessId);
            return ResponseEntity.ok(row);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @DeleteMapping("/clearCartByUser")
    public ResponseEntity<Void> clearCartByUser(@RequestParam String userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("cartCircuitBreaker");
        return circuitBreaker.run(() -> {
            cartService.clearCartByUser(userId);
            return ResponseEntity.noContent().build();
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }
}