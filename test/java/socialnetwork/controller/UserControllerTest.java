package socialnetwork.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Role;
import socialnetwork.domain.User;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;


//test class is focused on testing the web layer (MVC controllers)
// It will only load the components related to the specified controller
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc   
@Disabled
public class UserControllerTest {

    @Autowired   //used to inject a MockMvc instance into the test class
    private MockMvc mockMvc;  //allows me to perform HTTP requests and inspect the answers  

    @MockBean //create a mock of userService interface and inject it into the Spring context
    private UserService userService; 
    
    @Disabled()
    @Test
    void testCreateUser() throws Exception{
        //Arrange
        User newUser = User.builder().email("test@example.com").password("12345678").role(Role.USER).build();
        when(userService.createUser(any(User.class))).thenReturn(newUser);
        //Act and assert
        mockMvc.perform(post("/api/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\": \"test@example.com\", \"password\": \"2345678\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.password").value("12345678"));
        
        verify(userService, times(1)).createUser(any(User.class));
    }
    
    @Test
    @WithMockUser(
        value = "2",
        authorities = {"ADMIN"})
    void testGetAllUsers_WithUsers() throws Exception{
        //Arrange
        User newUser1 = User.builder().email("test1@example.com").password("12345678").role(Role.USER).build();
        User newUser2 = User.builder().email("test2@example.com").password("12345678").role(Role.USER).build();
        User newUser3 = User.builder().email("test3@example.com").password("12345678").role(Role.USER).build();
        User newUser4 = User.builder().email("test4@example.com").password("12345678").role(Role.USER).build();
        List<User> userList = Arrays.asList(newUser1, newUser2, newUser3, newUser4);
        when(userService.getAllUsers()).thenReturn(userList);

        //Act and asert
        mockMvc.perform(get("/api/users/getAllUsers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))  // $ of the json document
            .andExpect(jsonPath("$[0].email").value(newUser1.getEmail()))
            .andExpect(jsonPath("$[1].email").value("test2@example.com"))
            .andExpect(jsonPath("$[2].password").value("12345678"))
            .andExpect(jsonPath("$[3].role").value("USER"));

        verify(userService, times(1)).getAllUsers();
    }
    

    @Test
    @WithMockUser(
      value = "2",
      authorities = {"ADMIN"})
    void testGetUserByEmail_WithNoUsers() throws Exception{
        //Arrange
        List<User> userList = Arrays.asList();
        when(userService.getAllUsers()).thenReturn(userList);
        
        //Act and assert
        mockMvc.perform(get("/api/users/getAllUsers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
        
        verify(userService, times(1)).getAllUsers();
        
    }
    
    @Test
    @WithMockUser(
        value = "2",
        authorities = {"ADMIN"})
    void testGetUserById_ExitingUser() throws Exception{
        Long userId = 1L;
        User newUser = User.builder().id(userId).email("test@example.com").password("12345678").role(Role.USER).build();
        when(userService.getUserByID(userId)).thenReturn(Optional.of(newUser));

        //Act and assert
        mockMvc.perform(get("/api/users/getUserById/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserByID(userId);
    }

    @Test
    @WithMockUser(
        value = "2",
        authorities = {"ADMIN"})
    void testGetUserById_UserNotFound() throws Exception{
        //Arrange
        Long userId = 1L;
        when(userService.getUserByID(userId)).thenReturn(Optional.empty());

        //Act and assert
        mockMvc.perform(get("/api/users/getUserById/{userId}", userId))
            .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByID(userId);
    }

    @Test
    @WithMockUser(
        value = "2",
        authorities = {"ADMIN"})
    void testUpdateUser() throws Exception {
        // Arrange
        Long userId = 1L;
        User userToBeUpdated = User.builder().id(userId).email("oldUser@example.com").password("12345678").role(Role.USER).build();
        User newUpdatedUser = User.builder().id(userId).email("updated@example.com").password("2345678").role(Role.USER).build();
        when(userService.getUserByID(anyLong())).thenReturn(Optional.of(userToBeUpdated));
        when(userService.updateUser(any(User.class))).thenReturn(newUpdatedUser);
    
        // Act and assert
        mockMvc.perform(put("/api/users/updateUser/{userId}", userId) 
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"1\", \"email\": \"updated@example.com\", \"password\": \"2345678\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.password").value("2345678"));
    }
    
    @Test
    @WithMockUser(
        value = "2",
        authorities = {"ADMIN"})
    void testDeleteUser_UserExists() throws Exception{
        Long userId = 1L;
        User newUser = User.builder().id(userId).email("test@example.com").password("12345678").role(Role.USER).build();
        when(userService.getUserByID(userId)).thenReturn(Optional.of(newUser));
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/deleteUser/{userId}", userId))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception{
        Long nonExistingUserId = 1L;
        doThrow(EmptyResultDataAccessException.class).when(userService).deleteUser(nonExistingUserId);

        mockMvc.perform(delete("/api/users/deleteUser/{userId}", nonExistingUserId))
            .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(value = "2", authorities = {"ADMIN"})
    void testCreateFriendship_Succes() throws Exception{
        //Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        doReturn(Optional.of(new Friendship())).when(userService).createFriendship(userId1, userId2);

        //Act and Assert
        mockMvc.perform(post("/api/users/{userId1}/friendship/{userId2}", userId1, userId2))
            .andExpect(status().isOk());

    }

    @Test
    void testCreateFriendship_UserNotFound() throws Exception{
        //Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        doThrow(UserNotFoundException.class).when(userService).createFriendship(userId1, userId2);

        //Act and Assert
        mockMvc.perform(post("/api/users/{userId1}/friendship/{userId2}", userId1, userId2))
            .andExpect(status().isNotFound());

    }

    
    @Test
    void testDeleteFriendshipById() {
        
    }


    @Test
    void testDeleteUserWithError() {

    }

    @Test
    void testGetAllFriendshipsByUserId() {

    }

}
