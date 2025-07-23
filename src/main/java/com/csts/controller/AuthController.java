package com.csts.controller;
import com.csts.dto.LoginRequest;
import com.csts.dto.LoginResponse;
import com.csts.dto.RegisterRequest;
import com.csts.model.User;
import com.csts.security.OpaqueTokenService;
import com.csts.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private OpaqueTokenService tokenService;

    @Operation(summary = "Register user", description = "Register a new user with role")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        try {
            User registeredUser = authService.register(request);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Login user", description = "Authenticate user and return opaque token")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        try {
            User user = authService.authenticate(request);
            if (user != null) {
                String token = tokenService.generateToken(user);
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed: "+ e.getMessage());
        }
    }
}