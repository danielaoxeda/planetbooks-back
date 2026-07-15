package com.rodrigomv.planetbooksback.config;

import com.rodrigomv.planetbooksback.model.entity.User;
import com.rodrigomv.planetbooksback.model.enums.Role;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService svc;

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User u = User.builder().id(5L).email("a@b").password("p").enabled(true).role(Role.USER).build();
        when(userRepository.findByEmail("a@b")).thenReturn(Optional.of(u));

        UserDetails details = svc.loadUserByUsername("a@b");
        assertThat(details.getUsername()).isEqualTo("a@b");
        assertThat(details.getAuthorities()).isNotEmpty();
    }

    @Test
    void loadUserByUsername_shouldThrow() {
        when(userRepository.findByEmail("x")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> svc.loadUserByUsername("x"));
    }
}
