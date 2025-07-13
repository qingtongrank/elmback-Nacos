package ynu.elm.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ynu.elm.entity.Food;
import ynu.elm.service.FoodService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/FoodController")
public class FoodController {

    @Resource
    private FoodService foodService;

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/listFoodByBusiness")
    public ResponseEntity<List<Food>> listFoodByBusiness(@RequestParam String businessId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("foodCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<Food> list = foodService.listFoodByBusiness(businessId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/getFoodById")
    public ResponseEntity<Food> getFoodById(@RequestParam String foodId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("foodCircuitBreaker");
        return circuitBreaker.run(() -> {
            Optional<Food> food = foodService.getFoodById(foodId);
            return food.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/saveFood")
    public ResponseEntity<Food> saveFood(@RequestBody Food food) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("foodCircuitBreaker");
        return circuitBreaker.run(() -> {
            Food saved = foodService.saveFood(food);
            return ResponseEntity.ok(saved);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @DeleteMapping("/removeFood")
    public ResponseEntity<Void> removeFood(@RequestParam String foodId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("foodCircuitBreaker");
        return circuitBreaker.run(() -> {
            foodService.removeFood(foodId);
            return ResponseEntity.noContent().build();
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }
}