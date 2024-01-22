package socialnetwork.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import socialnetwork.domain.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    boolean existsByUserId1AndUserId2(Long userId1, Long userId2);
    void deleteByUserId1AndUserId2(Long userId1, Long userId2);
    Optional<Friendship> findByUserId1AndUserId2(Long userId1, Long userId2);
    @Query("SELECT f FROM Friendship f WHERE f.userId1 = :userId OR f.userId2 = :userId")
    List<Friendship> getAllFriendshipsByUserId(@Param("userId") Long userId);

    // :XX is used to denote a named parameter where XX is the name of the parameter 
    @Query("Select CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f " + 
    "WHERE (f.userId1 = :userId1 AND f.userId2 = :userId2) OR (f.userId1 = :userId2 AND f.userId2 = :userId1)")
    // a friendship between 2 users exists
    boolean existsByBothUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
