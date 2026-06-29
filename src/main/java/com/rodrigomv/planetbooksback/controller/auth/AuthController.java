package com.rodrigomv.planetbooksback.controller.auth;

import com.rodrigomv.planetbooksback.config.JwtTokenProvider;
import com.rodrigomv.planetbooksback.model.dto.LoginRequestDTO;
import com.rodrigomv.planetbooksback.model.dto.UserDTO;
import com.rodrigomv.planetbooksback.model.dto.UserRegistrationDTO;
import com.rodrigomv.planetbooksback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO user = userService.registerUser(registrationDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(loginRequest.getEmail());

        UserDTO userDTO = userService.getUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");
        response.put("user", userDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> validateSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        UserDTO user = userService.getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }
}
