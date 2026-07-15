package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.ChangePasswordDTO;
import com.rodrigomv.planetbooksback.model.dto.UserRegistrationDTO;
import com.rodrigomv.planetbooksback.model.entity.User;
import com.rodrigomv.planetbooksback.model.enums.Role;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        // MockitoExtension takes care of init
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
            .name("Juan")
            .email("juan@example.com")
            .password("secret")
            .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedSecret");

        User saved = User.builder()
            .id(1L)
            .name(dto.getName())
            .email(dto.getEmail())
            .password("encodedSecret")
            .role(Role.USER)
            .enabled(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        when(userRepository.save(any(User.class))).thenReturn(saved);

        var result = userService.registerUser(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());

        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertThat(captured.getPassword()).isEqualTo("encodedSecret");
        assertThat(captured.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
            .name("Ana")
            .email("ana@example.com")
            .password("pass")
            .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        Long userId = 42L;
        User existing = User.builder()
            .id(userId)
            .name("Carlos")
            .email("carlos@example.com")
            .password("encodedCurrent")
            .role(Role.USER)
            .enabled(true)
            .build();

        ChangePasswordDTO dto = new ChangePasswordDTO("current","newpass","newpass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("current", existing.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newpass", existing.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNew");

        userService.changePassword(userId, dto);

        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved.getPassword()).isEqualTo("encodedNew");
    }
}
