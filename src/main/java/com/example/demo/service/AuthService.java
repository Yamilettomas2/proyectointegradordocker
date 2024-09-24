package com.example.demo.service;

import com.example.demo.config.JwtService;
import com.example.demo.dto.AuthDto;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {
    @Autowired
    private MongoRepository mongoRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthDto login(LoginDto loginDto) {
        Optional<UserEntity> userOptional = mongoRepository.findByEmail(loginDto.getUsername());

        if (userOptional.isEmpty()) {
            return null;
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Authentication error", e);
        }

        UserEntity user = userOptional.get();
        String token = jwtService.getToken(user);

        return new AuthDto(token);
    }

    public AuthDto register(RegisterDto registerDto) {
        if (registerDto.getPassword().isEmpty() || registerDto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password");
        }

        Optional<UserEntity> existingUser = mongoRepository.findByEmail(registerDto.getEmail());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setName(registerDto.getName());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        try {
            mongoRepository.save(newUser);
        } catch (Exception e) {
            throw new RuntimeException("Error during saving user", e);
        }

        String token = jwtService.getToken(newUser);

        return new AuthDto(token);
    }
}
