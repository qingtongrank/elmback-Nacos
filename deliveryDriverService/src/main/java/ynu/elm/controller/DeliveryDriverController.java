package ynu.elm.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ynu.elm.entity.DeliveryDriver;
import ynu.elm.entity.DriverLoginResp;
import ynu.elm.service.DeliveryDriverService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/DeliveryDriverController")
public class DeliveryDriverController {

    @Resource
    private DeliveryDriverService deliveryDriverService;

    private final String secret = "wang-yong-shuo-w-y-s-12345678901";

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/getDriverByIdByPass")
    public ResponseEntity<DriverLoginResp> getDriverByIdByPass(@RequestParam String deliveryDriverId,
                                                               @RequestParam String password) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryDriverCircuitBreaker");
        return circuitBreaker.run(() -> {
            Optional<DeliveryDriver> driverOptional = deliveryDriverService.getDriverByIdByPass(deliveryDriverId, password);
            if (driverOptional.isPresent()) {
                String token = Jwts.builder()
                        .setSubject(driverOptional.get().getDeliveryDriverId())
                        .claim("role", "delivery_driver")
                        .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                        .compact();

                DriverLoginResp resp = new DriverLoginResp();
                resp.setDriver(driverOptional.get());
                resp.setToken(token);
                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.notFound().build();
            }
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/getDriverExistsById")
    public ResponseEntity<Boolean> getDriverExistsById(@RequestParam String deliveryDriverId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryDriverCircuitBreaker");
        return circuitBreaker.run(() -> {
            boolean exist = deliveryDriverService.isDriverExist(deliveryDriverId);
            return ResponseEntity.ok(exist);
        }, throwable -> {
            // 熔断时的回调逻辑
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/getAllDeliveryDriver")
    public ResponseEntity<List<DeliveryDriver>> getAllDeliveryDriver() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryDriverCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<DeliveryDriver> list = deliveryDriverService.getAllDeliveryDriver();
            return ResponseEntity.ok(list);
        }, throwable -> {
            // 熔断时的回调逻辑
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/saveDriver")
    public ResponseEntity<DeliveryDriver> saveDriver(@RequestBody DeliveryDriver driver) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("deliveryDriverCircuitBreaker");
        return circuitBreaker.run(() -> {
            try {
                DeliveryDriver saved = deliveryDriverService.saveDriver(driver);
                return ResponseEntity.ok(saved);
            } catch (DataAccessException e) {
                if (e instanceof DuplicateKeyException) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                throw e;
            }
        }, throwable -> {
            // 熔断时的回调逻辑
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }
}