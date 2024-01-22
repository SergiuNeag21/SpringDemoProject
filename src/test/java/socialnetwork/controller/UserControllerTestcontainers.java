package socialnetwork.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Role;
import socialnetwork.domain.User;
import socialnetwork.repository.FriendshipRepository;
import socialnetwork.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerTestcontainers {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() { 
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    @BeforeEach
    void setup() {
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Autowired // used to inject a MockMvc instance into the test class
    private MockMvc mockMvc; // allows me to perform HTTP requests and inspect the answers


    @Test
    @WithMockUser(
        value = "2",
        authorities = {"ADMIN"})   
    void testGetAllUsers_WithUsers() throws Exception {
        // Arrange
        User newUser1 = User.builder().email("test1@example.com").password("12345678").role(Role.USER).build();
        User newUser2 = User.builder().email("test2@example.com").password("12345678").role(Role.USER).build();
        User newUser3 = User.builder().email("test3@example.com").password("12345678").role(Role.USER).build();
        User newUser4 = User.builder().email("test4@example.com").password("12345678").role(Role.USER).build();
        List<User> userList = Arrays.asList(newUser1, newUser2, newUser3, newUser4);
        userRepository.saveAll(userList);

        // Act and assert
        mockMvc.perform(get("/api/users/getAllUsers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))  // $ of the json document
            .andExpect(jsonPath("$[0].email").value(newUser1.getEmail()))
            .andExpect(jsonPath("$[1].email").value("test2@example.com"))
            .andExpect(jsonPath("$[2].password").value("12345678"))
            .andExpect(jsonPath("$[3].role").value("USER"));

    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})  
    void testGetAllUsers_WitnNoUser() throws Exception{
        //Arragne
        List<User> userlist = new ArrayList<>();
        userRepository.saveAll(userlist);

        //Act and assert
        mockMvc.perform(get("/api/users/getAllUsers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})  
    void testDeleteUser_DeleteSingleUser() throws Exception{
        //Arrange
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User savedUser = userRepository.save(newUser1);

        //Act and assert
        mockMvc.perform(delete("/api/users/deleteUser/{userId}", savedUser.getId()))
            .andExpect(status().isOk());
        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})  
    void testDeleteUser_DeleteMultipleUsers() throws Exception{
        //Arrange
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User newUser2 = User.builder().email("example2@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        User savedUser2 = userRepository.save(newUser2);

        //Act and assert
        mockMvc.perform(delete("/api/users/deleteUser/{userId}", savedUser1.getId()))
            .andExpect(status().isOk());
        assertFalse(userRepository.findById(savedUser1.getId()).isPresent());
        assertFalse(userRepository.findAll().isEmpty());

        mockMvc.perform(delete("/api/users/deleteUser/{userId}", savedUser2.getId()))
            .andExpect(status().isOk());
        assertFalse(userRepository.findById(savedUser2.getId()).isPresent());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})  
    void testDeleteUser_UserNotFound() throws Exception{
        //Arrange
        List<User> userList = new ArrayList<>();
        userRepository.saveAll(userList);
        //Act and assert
        mockMvc.perform(delete("/api/users/deleteUser/{userId}",1L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void testGetUserById_UserFound() throws Exception{
        //Arrange
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User savedUser = userRepository.save(newUser1);

        //Act and assert
        mockMvc.perform(get("/api/users/getUserById/{userId}", savedUser.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedUser.getId()))
            .andExpect(jsonPath("$.email").value("example1@nia.com"));
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void testGetUserById_UserNotFound() throws Exception{
        //Arrange
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User savedUser = userRepository.save(newUser1);

        //Act and assert
        mockMvc.perform(get("/api/users/getUserById/{userId}", savedUser.getId()+1))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void testCreateFriendship_Succes() throws Exception{
        //Arrange
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User newUser2 = User.builder().email("example2@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        User savedUser2 = userRepository.save(newUser2);

        //Act and assert
        mockMvc.perform(post("/api/users/{userId1}/createFriendship/{userId2}", newUser1.getId(), newUser2.getId()))
            .andExpect(status().isOk());

        Optional<Friendship> friendship = friendshipRepository.findByUserId1AndUserId2(savedUser1.getId(), savedUser2.getId());
        assertNotNull(friendship);


    }
    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void testCreateFriendship_UserNotFound() throws Exception{
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        mockMvc.perform(post("/api/users/{userId1}/createFriendship/{userId2}", savedUser1.getId(), savedUser1.getId()+1))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void testCreateFriendship_FriendshipAlreadyExists() throws Exception{
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User newUser2 = User.builder().email("example2@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        User savedUser2 = userRepository.save(newUser2);
        Long userId1 = savedUser1.getId();
        Long userId2 = savedUser2.getId();
        Friendship friendship = new Friendship(userId1, userId2);
        friendshipRepository.save(friendship);
        mockMvc.perform(post("/api/users/{userId1}/createFriendship/{userId2}", userId1, userId2))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void testCreateFriendship_SameUserFriendship() throws Exception{
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        Long userId1 = savedUser1.getId();
        Friendship friendship = new Friendship(userId1, userId1);
        friendshipRepository.save(friendship);
        mockMvc.perform(post("/api/users/{userId1}/createFriendship/{userId2}", userId1, userId1))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void deteleteFriendship_Succesfull() throws Exception{
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User newUser2 = User.builder().email("example2@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        User savedUser2 = userRepository.save(newUser2);
        Long userId1 = savedUser1.getId();
        Long userId2 = savedUser2.getId();
        Friendship friendship = new Friendship(userId1, userId2);
        friendshipRepository.save(friendship);

        // X
        assertFalse(friendshipRepository.findAll().isEmpty());

        mockMvc.perform(delete("/api/users/{userId1}/deleteFriendship/{userId2}", userId1, userId2))
            .andExpect(status().isOk());

        assertTrue(friendshipRepository.findAll().isEmpty());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void deteleteFriendship_NotExistingFriendship() throws Exception{
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User newUser2 = User.builder().email("example2@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        User savedUser2 = userRepository.save(newUser2);
        Long userId1 = savedUser1.getId();
        Long userId2 = savedUser2.getId();

        mockMvc.perform(delete("/api/users/{userId1}/deleteFriendship/{userId2}", userId1, userId2))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void deteleteFriendship_UserNotFound() throws Exception{
        mockMvc.perform(delete("/api/users/{userId1}/deleteFriendship/{userId2}", 1L, 2L))
            .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void getAllFriendshipsByUserId_Succesfull() throws Exception{
        User newUser1 = User.builder().email("example1@nia.com").password("password").role(Role.USER).build();
        User newUser2 = User.builder().email("example2@nia.com").password("password").role(Role.USER).build();
        User newUser3 = User.builder().email("example3@nia.com").password("password").role(Role.USER).build();
        User savedUser1 = userRepository.save(newUser1);
        User savedUser2 = userRepository.save(newUser2);
        User savedUser3 = userRepository.save(newUser3);
        Long userId1 = savedUser1.getId();
        Long userId2 = savedUser2.getId();
        Long userId3 = savedUser3.getId();
        Friendship friendship1 = new Friendship(userId1, userId2);
        Friendship friendship2 = new Friendship(userId1, userId3);
        Friendship friendship3 = new Friendship(userId2, userId3);
        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);
        friendshipRepository.save(friendship3);

        mockMvc.perform(get("/api/users/{userId1}/getAllFriendshipsByUserId", userId1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void getAllFriendshipsByUserId_UserNotFound() throws Exception{
        mockMvc.perform(get("/api/users/{userId1}/getAllFriendshipsByUserId", 1L))
            .andExpect(status().isNotFound());
    }


}
