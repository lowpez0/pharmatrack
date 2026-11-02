package pharmatrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmatrack.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
