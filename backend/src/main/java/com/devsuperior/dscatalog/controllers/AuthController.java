package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.requests.UserRecoveryPasswordRequest;
import com.devsuperior.dscatalog.services.AuthService;
import com.devsuperior.dscatalog.services.EmailServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody UserRecoveryPasswordRequest request) {
        authService.createRecoveryToken(request.getEmail());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
