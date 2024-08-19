package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.requests.NewPasswordRequest;
import com.devsuperior.dscatalog.dto.requests.UserRecoveryPasswordRequest;
import com.devsuperior.dscatalog.dto.responses.ProfileResponse;
import com.devsuperior.dscatalog.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getCurrentUser() {
        ProfileResponse result = authService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody UserRecoveryPasswordRequest request) {
        authService.createRecoveryToken(request.getEmail());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/new-password")
    public ResponseEntity<Void> setNewPassword(@Valid @RequestBody NewPasswordRequest request) {
        authService.setNewPassword(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
