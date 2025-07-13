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
import ynu.elm.entity.Admin;
import ynu.elm.entity.AdminLoginResp;
import ynu.elm.service.AdminService;

import java.util.Optional;

@RestController
@RequestMapping("/AdminController")
public class AdminController {

    @Resource
    private AdminService adminService;

    private final String secret = "wang-yong-shuo-w-y-s-12345678901";

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/getAdminByIdByPass")
    public ResponseEntity<AdminLoginResp> getAdminByIdByPass(@RequestParam String adminId,
                                                             @RequestParam String password) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("adminCircuitBreaker");
        return circuitBreaker.run(() -> {
            Optional<Admin> adminOptional = adminService.getAdminByIdByPass(adminId, password);
            if (adminOptional.isPresent()) {
                String token = Jwts.builder()
                        .setSubject(adminOptional.get().getAdminId())
                        .claim("role", "admin")
                        .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                        .compact();

                AdminLoginResp resp = new AdminLoginResp();
                resp.setAdmin(adminOptional.get());
                resp.setToken(token);
                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.notFound().build();
            }
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping("/getAdminExistsById")
    public ResponseEntity<Boolean> getAdminExistsById(@RequestParam String adminId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("adminCircuitBreaker");
        return circuitBreaker.run(() -> {
            boolean exist = adminService.isAdminExist(adminId);
            return ResponseEntity.ok(exist);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping("/saveAdmin")
    public ResponseEntity<Admin> saveAdmin(@RequestBody Admin admin) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("adminCircuitBreaker");
        return circuitBreaker.run(() -> {
            try {
                Admin saved = adminService.saveAdmin(admin);
                return ResponseEntity.ok(saved);
            } catch (DataAccessException e) {
                if (e instanceof DuplicateKeyException) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                throw e;
            }
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }
}