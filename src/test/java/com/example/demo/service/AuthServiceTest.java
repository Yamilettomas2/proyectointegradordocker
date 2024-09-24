package com.example.demo.service;

import com.example.demo.config.JwtService;
import com.example.demo.dto.AuthDto;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.MongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private MongoRepository mongoRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin() {
        LoginDto loginDto = new LoginDto("ejemplo@example.com", "123");
        UserEntity user = new UserEntity();
        user.setEmail("ejemplo@ejemplo.com");
        user.setPassword("hashedPassword");

        when(mongoRepository.findByEmail(loginDto.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(jwtService.getToken(user)).thenReturn("jwtToken");

        AuthDto result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
    }

    @Test
    void testLoginUserNotFound() {
        LoginDto loginDto = new LoginDto("ejemplo@example.com", "123");

        when(mongoRepository.findByEmail(loginDto.getUsername())).thenReturn(Optional.empty());

        AuthDto result = authService.login(loginDto);

        assertNull(result);
    }

    @Test
    void testLoginAuthenticationError() {
        LoginDto loginDto = new LoginDto("ejemplo@example.com", "123");
        UserEntity user = new UserEntity();
        user.setEmail("error@ejemplo.com");

        when(mongoRepository.findByEmail(loginDto.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Error de autenticación"));

        assertThrows(RuntimeException.class, () -> authService.login(loginDto));
    }

    @Test
    void testRegister() {
        RegisterDto registerDto = new RegisterDto("batres", "batres@ejemplo.com", "password");
        UserEntity user = new UserEntity();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword("hashedPassword");

        when(mongoRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("hashedPassword");
        when(mongoRepository.save(any(UserEntity.class))).thenReturn(user);
        when(jwtService.getToken(argThat(u -> u instanceof UserEntity && ((UserEntity) u).getEmail().equals(user.getEmail())))).thenReturn("jwtToken");

        AuthDto result = authService.register(registerDto);

        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
    }


    @Test
    void testRegisterUserAlreadyExists() {
        RegisterDto registerDto = new RegisterDto("yamilet", "yamilet@ejemplo.com", "contrasena");

        when(mongoRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(new UserEntity()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerDto));
    }

    @Test
    void testRegisterInvalidPassword() {
        RegisterDto registerDto = new RegisterDto("yamilet", "yamilet@ejemplo.com", "");

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerDto));
    }
}
