package socialnetwork.domain;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId1;

    private Long userId2;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    private LocalDateTime createdAt;

    public enum FriendshipStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
    }

    public Friendship(Long userId1, Long userId2){
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = FriendshipStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
}
