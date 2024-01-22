package socialnetwork.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import socialnetwork.domain.User;


public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    boolean existsById(Long id);

    //both users exists in db
    @Query("SELECT CASE WHEN COUNT(u) = 2 THEN true ELSE false END FROM User u " +
    "WHERE u.id IN (:userId1, :userId2)")
    boolean existsByBothUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
