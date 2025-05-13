package com.molkov.otpservice.service;

import com.molkov.otpservice.dto.UserDto;
import com.molkov.otpservice.model.user.User;
import com.molkov.otpservice.model.user.UserRole;
import com.molkov.otpservice.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void create_shouldCreateNewUser() {
        // Arrange
        UserDto userDto = new UserDto(
                "testUser",
                "password",
                UserRole.USER,
                null,
                "test@example.com",
                null);
        when(userRepository.findByUsername(userDto.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User result = userService.create(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(userDto.username(), result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(UserRole.USER, result.getRole());
        verify(userRepository).save(any(User.class));

    }

    @Test
    public void create_WhenUpdateUsernameExists_ShouldThrowException() {
        // Arrange
        UserDto userDto = new UserDto(
                "existingUsername",
                "password",
                UserRole.USER,
                null,
                "test@example.com",
                null
        );
        User existingUser = new User();
        existingUser.setUsername(userDto.username());

        when(userRepository.findByUsername(userDto.username())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> userService.create(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void findByUsername_shouldReturnUser() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    public void loadUserByUsername_ShouldReturnUserDetails() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setRole(UserRole.USER);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
    }
}
