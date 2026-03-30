package com.rusty_rhino.rusty_api.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final String adminUser1;
    private final String adminPass1;
    private final String adminUser2;
    private final String adminPass2;

    public AuthController(
            JwtUtil jwtUtil,
            @Value("${auth.admin.user1.username:}") String adminUser1,
            @Value("${auth.admin.user1.password:}") String adminPass1,
            @Value("${auth.admin.user2.username:}") String adminUser2,
            @Value("${auth.admin.user2.password:}") String adminPass2) {
        this.jwtUtil = jwtUtil;
        this.adminUser1 = adminUser1;
        this.adminPass1 = adminPass1;
        this.adminUser2 = adminUser2;
        this.adminPass2 = adminPass2;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (isValidCredentials(request.getUsername(), request.getPassword())) {
            String token = jwtUtil.generateToken(request.getUsername());
            log.info("Successful login for user: {}", request.getUsername());
            return ResponseEntity.ok(new LoginResponse(token, request.getUsername()));
        }
        log.warn("Failed login attempt for user: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    private boolean isValidCredentials(String username, String password) {
        if (username == null || password == null) return false;
        return (adminUser1.equals(username) && adminPass1.equals(password))
                || (adminUser2.equals(username) && adminPass2.equals(password));
    }
}
