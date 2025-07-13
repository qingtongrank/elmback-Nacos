package ynu.elm.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ynu.elm.entity.Business;
import ynu.elm.entity.BusinessLoginResp;
import ynu.elm.service.BusinessService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/BusinessController")
public class BusinessController {

    @Resource
    private BusinessService businessService;

    private final String secret = "wang-yong-shuo-w-y-s-12345678901";

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/getBusinessByIdByPass")
    public ResponseEntity<BusinessLoginResp> getBusinessByIdByPass(@RequestParam String businessId,
                                                                   @RequestParam String password) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("businessCircuitBreaker");
        return circuitBreaker.run(() -> {
            Optional<Business> businessOptional = businessService.getBusinessByIdByPass(businessId, password);
            if (businessOptional.isPresent()) {
                String token = Jwts.builder()
                        .setSubject(businessOptional.get().getBusinessId())
                        .claim("role", "business")
                        .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                        .compact();

                BusinessLoginResp resp = new BusinessLoginResp();
                resp.setBusiness(businessOptional.get());
                resp.setToken(token);
                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.notFound().build();
            }
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/getBusinessExistsById")
    public ResponseEntity<Business> getBusinessExistsById(@RequestParam String businessId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("businessCircuitBreaker");
        return circuitBreaker.run(() -> {
            Optional<Business> business = businessService.getBusinessById(businessId);
            return ResponseEntity.ok(business.get());
        }, throwable -> {
            // 熔断时的回调逻辑
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/listBusinessByOrderTypeId")
    public ResponseEntity<List<Business>> listBusinessByOrderTypeId(@RequestParam Integer orderTypeId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("businessCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<Business> list = businessService.listBusinessByOrderTypeId(orderTypeId);
            return ResponseEntity.ok(list);
        }, throwable -> {
            // 熔断时的回调逻辑
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/getAllBusinesses")
    public ResponseEntity<List<Business>> getAllBusinesses() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("businessCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<Business> list = businessService.getAllBusinesses();
            return ResponseEntity.ok(list);
        }, throwable -> {
            // 熔断时的回调逻辑
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/saveBusiness")
    public ResponseEntity<Business> saveBusiness(@RequestBody Business business) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("businessCircuitBreaker");
        return circuitBreaker.run(() -> {
            try {
                Business saved = businessService.saveBusiness(business);
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