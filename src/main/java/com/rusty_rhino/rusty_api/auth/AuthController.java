package com.rusty_rhino.rusty_api.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
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
    private final Map<String, String> adminCredentials = new HashMap<>();

    public AuthController(JwtUtil jwtUtil, Environment env) {
        this.jwtUtil = jwtUtil;
        for (int i = 1; ; i++) {
            String username = env.getProperty("auth.admin.user" + i + ".username");
            String password = env.getProperty("auth.admin.user" + i + ".password");
            if (username == null || username.isBlank()) break;
            adminCredentials.put(username, password);
        }
        log.info("Loaded {} admin user(s)", adminCredentials.size());
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
        String expected = adminCredentials.get(username);
        return expected != null && expected.equals(password);
    }
}
