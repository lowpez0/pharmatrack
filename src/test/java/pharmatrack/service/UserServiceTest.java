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

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> users = List.of(
                createTestUser(),
                createTestUser()
        );

        when(userRepo.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);

        verify(userRepo).findAll();
    }


    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {

        User user = createTestUser();

        when(userRepo.findById(1))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(userRepo).findById(1);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(1));

        verify(userRepo).findById(1);
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        User user = createTestUser();

        when(userRepo.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(userRepo).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByUsername("unknown"));

        verify(userRepo).findByUsername("unknown");
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUsernameIsUnchanged() {

        User existingUser = createTestUser();
        User updatedUser = createTestUser();

        updatedUser.setFullName("Updated Name");
        updatedUser.setRole(existingUser.getRole());

        when(userRepo.findById(1))
                .thenReturn(Optional.of(existingUser));

        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1, updatedUser);

        assertEquals("testuser", result.getUsername());
        assertEquals("Updated Name", result.getFullName());

        verify(userRepo).save(existingUser);
        verify(userRepo, never()).existsByUsername(anyString());
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenNewUsernameIsAvailable() {

        User existingUser = createTestUser();
        User updatedUser = createTestUser();

        updatedUser.setUsername("newusername");
        updatedUser.setFullName("Updated Name");
        updatedUser.setRole(existingUser.getRole());

        when(userRepo.findById(1))
                .thenReturn(Optional.of(existingUser));

        when(userRepo.existsByUsername("newusername"))
                .thenReturn(false);

        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1, updatedUser);

        assertEquals("newusername", result.getUsername());
        assertEquals("Updated Name", result.getFullName());

        verify(userRepo).existsByUsername("newusername");
        verify(userRepo).save(existingUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenNewUsernameAlreadyExists() {

        User existingUser = createTestUser();
        User updatedUser = createTestUser();

        updatedUser.setUsername("takenusername");

        when(userRepo.findById(1))
                .thenReturn(Optional.of(existingUser));

        when(userRepo.existsByUsername("takenusername"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1, updatedUser));

        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void deactivateUser_ShouldSetAccountStatusToInactive() {

        User user = createTestUser();

        when(userRepo.findById(1))
                .thenReturn(Optional.of(user));

        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.deactivateUser(1);

        assertEquals(AccountStatus.INACTIVE, result.getAccountStatus());

        verify(userRepo).save(user);
    }

    @Test
    void activateUser_ShouldSetAccountStatusToActive() {

        User user = createTestUser();
        user.setAccountStatus(AccountStatus.INACTIVE);

        when(userRepo.findById(1))
                .thenReturn(Optional.of(user));

        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.activateUser(1);

        assertEquals(AccountStatus.ACTIVE, result.getAccountStatus());

        verify(userRepo).save(user);
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenPasswordIsValid() {

        User user = createTestUser();

        when(userRepo.findById(1))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("newEncodedPassword");

        userService.changePassword(1, "newPassword123");

        assertEquals("newEncodedPassword", user.getPassword());

        verify(passwordEncoder).encode("newPassword123");
        verify(userRepo).save(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenPasswordIsNull() {

        User user = createTestUser();

        when(userRepo.findById(1))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(1, null));

        verify(userRepo, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void changePassword_ShouldThrowException_WhenPasswordIsBlank() {

        User user = createTestUser();

        when(userRepo.findById(1))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(1, "   "));

        verify(userRepo, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}

