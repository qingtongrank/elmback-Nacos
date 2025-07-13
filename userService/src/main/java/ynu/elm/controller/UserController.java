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
import ynu.elm.entity.User;
import ynu.elm.entity.UserLoginResp;
import ynu.elm.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/UserController")
public class UserController {

    @Resource
    private UserService userService;

    private final String secret = "wang-yong-shuo-w-y-s-12345678901";

    @Resource
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping(value = "/getUserByIdByPass")
    public ResponseEntity<UserLoginResp> getUserByIdByPass(@RequestParam(value = "userId") String userId, @RequestParam(value = "password") String password) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userCircuitBreaker");
        return circuitBreaker.run(() -> {
            Optional<User> userOptional = userService.getUserByIdByPass(userId, password);
            if (userOptional.isPresent()) {
                String token = Jwts.builder()
                        .setSubject(userOptional.get().getUserId().toString())
                        .claim("role", "user")
                        .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                        .compact();

                UserLoginResp resp = new UserLoginResp();
                System.out.println(resp);
                resp.setUser(userOptional.get());
                resp.setToken(token);

                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.notFound().build();
            }
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping(value = "/getUserById")
    public ResponseEntity<Boolean> getUserExistsById(@RequestParam(value = "userId") String userId) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userCircuitBreaker");
        return circuitBreaker.run(() -> {
            boolean userExist = userService.isUserExist(userId);
            return ResponseEntity.ok(userExist);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @GetMapping(value = "/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userCircuitBreaker");
        return circuitBreaker.run(() -> {
            List<User> list = userService.getAllUsers();
            return ResponseEntity.ok(list);
        }, throwable -> {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        });
    }

    @PostMapping(value = "/saveUser")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("userCircuitBreaker");
        return circuitBreaker.run(() -> {
            try {
                User savedUser = userService.saveUser(user);
                return ResponseEntity.ok(savedUser);
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