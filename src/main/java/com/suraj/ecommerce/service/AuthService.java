package com.suraj.ecommerce.service;

import com.suraj.ecommerce.dto.AuthResponse;
import com.suraj.ecommerce.dto.LoginRequest;
import com.suraj.ecommerce.dto.RegisterRequest;
import com.suraj.ecommerce.entity.User;
import com.suraj.ecommerce.repository.UserRepository;
import com.suraj.ecommerce.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username is already registered"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole("USER");
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getUsername(),
                savedUser.getRole()
        );

        return new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getRole(),
                "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        User user = userRepository.findByUsername(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        ));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalArgumentException(
                    "User account is inactive"
            );
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole(),
                "Login successful"
        );
    }
}