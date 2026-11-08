package pharmatrack.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import pharmatrack.entity.User;
import pharmatrack.entity.enums.AccountStatus;
import pharmatrack.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setFullName("Test User");
        return user;
    }

    @Test
    void createUser_ShouldCreateUser_WhenUsernameIsAvailable() {

        User user = createTestUser();

        when(userRepo.existsByUsername("testuser"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(AccountStatus.ACTIVE, result.getAccountStatus());

        verify(userRepo).existsByUsername("testuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepo).save(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameAlreadyExists() {

        User user = createTestUser();

        when(userRepo.existsByUsername("testuser"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(user));

        verify(userRepo, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}

