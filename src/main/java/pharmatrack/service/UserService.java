package pharmatrack.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pharmatrack.entity.User;
import pharmatrack.entity.enums.AccountStatus;
import pharmatrack.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if(userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountStatus(AccountStatus.ACTIVE);

        return userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(int userId) {

        return userRepo.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found with ID: " + userId
                        ));
    }

    public User getUserByUsername(String username) {

        return userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found with username: " + username
                        ));
    }

    public User updateUser(int userId, User updatedUser) {
        User existingUser = getUserById(userId);

        if (!existingUser.getUsername().equals(updatedUser.getUsername())
                && userRepo.existsByUsername(updatedUser.getUsername())) {

            throw new IllegalArgumentException("Username already exists.");
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setRole(updatedUser.getRole());

        return userRepo.save(existingUser);
    }

    public User deactivateUser(int userId) {
        User user = getUserById(userId);
        user.setAccountStatus(AccountStatus.INACTIVE);

        return userRepo.save(user);
    }

    public User activateUser(int userId) {
        User user = getUserById(userId);
        user.setAccountStatus(AccountStatus.ACTIVE);

        return userRepo.save(user);
    }

    public void changePassword(int userId, String newPassword) {
        User user = getUserById(userId);
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepo.save(user);
    }
}
