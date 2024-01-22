package socialnetwork.repository;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import socialnetwork.domain.Role;
import socialnetwork.domain.User;

//test annotation that configures a slice of the application context for testing JPA repositories.
@DataJpaTest
// @ActiveProfiles("test")
// @TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryTest {

    //it tells Spring to inject an instance of the required dependency, such as a repository or service, at runtime.
    @Autowired
    private UserRepository userRepository;

    @Test
    void testExistsById() {
        //Arrange
        User user = User.builder().email("sergiu@nia.com").password("12345678").role(Role.USER).build();
        User user1 = User.builder().email("user1@example.com").password("password").role(Role.USER).build();
        User user2 = User.builder().email("user2@example.com").password("password").role(Role.USER).build();
        //Act
        User savedUser = userRepository.save(user);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        //Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(userRepository.existsById(savedUser.getId())).isTrue();
        Assertions.assertThat(userRepository.existsById(savedUser1.getId())).isTrue();
        Assertions.assertThat(userRepository.existsById(savedUser2.getId())).isTrue();
        Assertions.assertThat(userRepository.existsById(999L)).isFalse();
  
    }

    @Test
    void testExistsByBothUsers() {
        //Arrange
        User user1 = User.builder().email("user1@example.com").password("password").role(Role.USER).build();
        User user2 = User.builder().email("user2@example.com").password("password").role(Role.USER).build();
        //Act
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        //Assert
        Assertions.assertThat(userRepository.existsByBothUsers(savedUser1.getId(), savedUser2.getId())).isTrue();
        Assertions.assertThat(userRepository.existsByBothUsers(savedUser1.getId(), 999L)).isFalse();
    }

    @Test
    void testFindByEmail() {
        //Arrange 
        User user1 = User.builder().email("user1@example.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(user1);
        //Act
        Optional<User> foundUser = userRepository.findByEmail(savedUser1.getEmail());
        //Assert
        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(userRepository.findByEmail(savedUser1.getEmail())).isEqualTo(Optional.ofNullable(savedUser1));
    }
}
