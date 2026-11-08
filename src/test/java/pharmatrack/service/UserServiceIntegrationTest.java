
package pharmatrack.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import pharmatrack.entity.User;
import pharmatrack.entity.enums.AccountStatus;
import pharmatrack.entity.enums.UserRole;
import pharmatrack.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;


    private User createTestUser(String username) {

        User user = new User();

        user.setUsername(username);
        user.setPassword("password123");
        user.setFullName("Test User");
        user.setRole(UserRole.ADMIN);

        return user;
    }

    // saves user and retrieves it from the database
    private User persistTestUser(String username) {

        User user = userService.createUser(
                createTestUser(username)
        );

        entityManager.flush();
        int userId = user.getUserId();

        entityManager.clear();

        return userService.getUserById(userId);
    }


    //CREATE USER ===================================================

    @Test
    void createUser_ShouldSaveUserToDatabase() {

        User user = createTestUser("integration_create");

        User savedUser = userService.createUser(user);

        entityManager.flush();

        int userId = savedUser.getUserId();

        entityManager.clear();

        User retrievedUser = userService.getUserById(userId);

        assertNotNull(retrievedUser);
        assertEquals("integration_create", retrievedUser.getUsername());
        assertEquals("Test User", retrievedUser.getFullName());
        assertEquals(AccountStatus.ACTIVE, retrievedUser.getAccountStatus());

        assertNotEquals("password123", retrievedUser.getPassword());

        assertTrue(
                passwordEncoder.matches(
                        "password123",
                        retrievedUser.getPassword()
                )
        );
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameAlreadyExists() {

        persistTestUser("integration_duplicate");

        User duplicateUser = createTestUser("integration_duplicate");

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(duplicateUser));
    }


    // GET ALL USERS ======================================================

    @Test
    void getAllUsers_ShouldRetrieveUsersFromDatabase() {

        User user = persistTestUser("integration_getall");

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());

        assertTrue(
                users.stream().anyMatch(
                        u -> u.getUserId() == user.getUserId()
                )
        );
    }


    // GET USER BY ID ====================================================

    @Test
    void getUserById_ShouldRetrieveUserFromDatabase() {

        User user = persistTestUser("integration_getid");

        entityManager.clear();

        User retrievedUser = userService.getUserById(user.getUserId());

        assertNotNull(retrievedUser);
        assertEquals(user.getUserId(), retrievedUser.getUserId());
        assertEquals("integration_getid", retrievedUser.getUsername());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(-999));
    }


    // GET USER BY USERNAME =======================================

    @Test
    void getUserByUsername_ShouldRetrieveUserFromDatabase() {

        persistTestUser("integration_getusername");

        entityManager.clear();

        User retrievedUser =
                userService.getUserByUsername("integration_getusername");

        assertNotNull(retrievedUser);
        assertEquals("integration_getusername", retrievedUser.getUsername());
    }

    @Test
    void getUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByUsername("unknown_user"));
    }


    // UPDATE USER =======================================================

    @Test
    void updateUser_ShouldUpdateUserInformationInDatabase() {

        User existingUser = persistTestUser("integration_update");

        User updatedUser = createTestUser("integration_updated");
        updatedUser.setFullName("Updated Test User");
        updatedUser.setRole(UserRole.EMPLOYEE);

        User result = userService.updateUser(
                existingUser.getUserId(),
                updatedUser
        );

        entityManager.flush();
        entityManager.clear();

        User retrievedUser =
                userService.getUserById(existingUser.getUserId());

        assertEquals("integration_updated", retrievedUser.getUsername());
        assertEquals("Updated Test User", retrievedUser.getFullName());
        assertEquals(UserRole.EMPLOYEE, retrievedUser.getRole());
    }

    @Test
    void updateUser_ShouldThrowException_WhenUsernameAlreadyExists() {

        User existingUser = persistTestUser("integration_update1");

        persistTestUser("integration_update2");

        User updatedUser = createTestUser("integration_update2");

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(
                        existingUser.getUserId(),
                        updatedUser
                ));
    }


    // DEACTIVATE USER ====================================================

    @Test
    void deactivateUser_ShouldUpdateAccountStatusInDatabase() {

        User user = persistTestUser("integration_deactivate");

        userService.deactivateUser(user.getUserId());

        entityManager.flush();
        entityManager.clear();

        User retrievedUser =
                userService.getUserById(user.getUserId());

        assertEquals(AccountStatus.INACTIVE,
                retrievedUser.getAccountStatus());
    }


    // ACTIVATE USER ====================================================

    @Test
    void activateUser_ShouldUpdateAccountStatusInDatabase() {

        User user = persistTestUser("integration_activate");

        userService.deactivateUser(user.getUserId());

        userService.activateUser(user.getUserId());

        entityManager.flush();
        entityManager.clear();

        User retrievedUser =
                userService.getUserById(user.getUserId());

        assertEquals(AccountStatus.ACTIVE,
                retrievedUser.getAccountStatus());
    }


    // CHANGE PASSWORD ==================================================

    @Test
    void changePassword_ShouldUpdatePasswordInDatabase() {

        User user = persistTestUser("integration_password");

        userService.changePassword(
                user.getUserId(),
                "newPassword123"
        );

        entityManager.flush();
        entityManager.clear();

        User retrievedUser =
                userService.getUserById(user.getUserId());

        assertNotEquals("newPassword123", retrievedUser.getPassword());

        assertTrue(
                passwordEncoder.matches(
                        "newPassword123",
                        retrievedUser.getPassword()
                )
        );
    }

    @Test
    void changePassword_ShouldThrowException_WhenPasswordIsNull() {

        User user = persistTestUser("integration_nullpassword");

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(
                        user.getUserId(),
                        null
                ));
    }

    @Test
    void changePassword_ShouldThrowException_WhenPasswordIsBlank() {

        User user = persistTestUser("integration_blankpassword");

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(
                        user.getUserId(),
                        "   "
                ));
    }

}
