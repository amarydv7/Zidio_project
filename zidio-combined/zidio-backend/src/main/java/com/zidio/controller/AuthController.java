package com.zidio.controller;

import com.zidio.model.User;
import com.zidio.security.JwtUtil;
import com.zidio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password"); // expecting raw password
        if (email == null || password == null) return ResponseEntity.badRequest().body(Map.of("message","email and password required"));
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(password);
        u.setRole(com.zidio.model.Role.STUDENT);
        User saved = userService.register(u);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "email", saved.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) return ResponseEntity.badRequest().body(Map.of("message","email and password required"));
        User u = userService.findByEmail(email);
        if (u == null) return ResponseEntity.status(401).body(Map.of("message","invalid credentials"));
        boolean ok = userService.checkPassword(password, u.getPasswordHash());
        if (!ok) return ResponseEntity.status(401).body(Map.of("message","invalid credentials"));
        String token = jwtUtil.generateToken(u.getEmail(), u.getRole().name());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
