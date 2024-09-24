package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.MongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private MongoRepository mongoRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAll() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("yamilet");
        user.setEmail("yami@example.com");

        when(mongoRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAll();
        assertEquals(1, result.size());
        assertEquals("yamilet", result.get(0).getName());
        assertEquals("yami@example.com", result.get(0).getEmail());
    }

    @Test
    void testFindById() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("yami");
        user.setEmail("yami@example.com");

        when(mongoRepository.findById("1")).thenReturn(Optional.of(user));

        UserDto result = userService.getById("1");
        assertEquals("yami", result.getName());
        assertEquals("yami@example.com", result.getEmail());
    }

    @Test
    void testFindByIdNotFound() {
        when(mongoRepository.findById("1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.getById("1"));
    }

    @Test
    void testSave() {
        UserDto userDto = new UserDto();
        userDto.setName("Azucena");
        userDto.setEmail("azucena@ejemplo.com");

        UserEntity user = new UserEntity();
        user.setName("Azucena");
        user.setEmail("azucena@ejemplo.com");

        when(mongoRepository.save(any(UserEntity.class))).thenReturn(user);

        UserDto result = userService.save(userDto);
        assertEquals("Azucena", result.getName());
        assertEquals("azucena@ejemplo.com", result.getEmail());
    }

    @Test
    void testUpdate() {
        UserDto userDto = new UserDto();
        userDto.setName("Azucena");
        userDto.setEmail("azucena@ejemplo.com");

        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("yamilet");
        user.setEmail("yami@ejemplo.com");

        when(mongoRepository.findById("1")).thenReturn(Optional.of(user));
        when(mongoRepository.save(any(UserEntity.class))).thenReturn(user);

        UserDto result = userService.update(userDto, "1");
        assertEquals("Azucena", result.getName());
        assertEquals("azucena@ejemplo.com", result.getEmail());
    }

    @Test
    void testDeleteById() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("yami");
        user.setEmail("yami@ejemplo.com");
        when(mongoRepository.findById("1")).thenReturn(Optional.of(user));
        userService.delete("1");
        verify(mongoRepository, times(1)).delete(user);
    }
}
