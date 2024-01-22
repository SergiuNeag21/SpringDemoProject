package socialnetwork.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Role;
import socialnetwork.domain.User;
import socialnetwork.exceptions.ExistingFriendshipException;
import socialnetwork.exceptions.SameUserFriendshipException;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.repository.FriendshipRepository;
import socialnetwork.repository.UserRepository;

import java.util.List;
import java.util.Arrays;

import java.util.Optional;


// is a JUnit 5 annotation that integrates Mockito into tests
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        //Arrange
        User newUser = User.builder().email("user1@example.com").password("password").role(Role.USER).build();
        when(userRepository.save(newUser)).thenReturn(newUser);
        //Act
        User createdUser = userService.createUser(newUser);
        //Assert
        assertNotNull(createdUser);
        assertEquals("user1@example.com", createdUser.getEmail());
        assertEquals("password", createdUser.getPassword());
        assertEquals(Role.USER, createdUser.getRole());

        // Verify that the save method was called with the correct user
        verify(userRepository, times(1)).save(newUser);
    }
    

    @Test
    void testDeleteUser_UserFound() {
        // Arrange
        Long userId = 1L;

        // Act
        userService.deleteUser(userId);

        // Assert
        // Verify that the deleteById method was called with the correct user ID
        verify(userRepository, times(1)).deleteById(eq(userId));
    }

    // @Test
    // void testDeleteUser_UserNotFound() {
    //     // Arrange
    //     Long userId = 1L;
    //     when(userRepository.findById(userId)).thenReturn(Optional.empty());

    //     // Act
    //     userService.deleteUser(userId);

    //     // Assert
    //     // Verify that the findById method was called with the correct user ID
    //     verify(userRepository, times(1)).findById(eq(userId));

    //     // Verify that the deleteById method was not called
    //     verify(userRepository, never()).deleteById(anyLong());
    // }
    
    @Test
    void testGetAllUsers() {
        // Arrange
        User newUser1 = User.builder().email("user1@example.com").password("password").role(Role.USER).build();
        User newUser2 = User.builder().email("user2@example.com").password("password").role(Role.USER).build();
        List<User> userList = Arrays.asList(newUser1, newUser2);
        when(userRepository.findAll()).thenReturn(userList);
        // Act
        List<User> resultUserList = userService.getAllUsers();

        // Assert
        assertNotNull(resultUserList);
        assertEquals(2, resultUserList.size());
        assertEquals(newUser1, resultUserList.get(0));
        assertEquals(newUser2, resultUserList.get(1));

        verify(userRepository, times(1)).findAll();
        
    }
    
    @Test
    void testCreateFriendship_Succes() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        when(userRepository.existsByBothUsers(userId1, userId2)).thenReturn(true);
        when(friendshipRepository.existsByBothUserIds(userId1, userId2)).thenReturn(false);
        // Act
        Optional<Friendship> friendshipResult = userService.createFriendship(userId1, userId2);
        //Assert
        assertTrue(friendshipResult.isPresent());
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    void testCreateFriendship_SameUser(){
        // Arrange
        Long userId = 1L;
        // Act and Assert
        assertThrows(SameUserFriendshipException.class, () -> userService.createFriendship(userId, userId));
        verify(userRepository, never()).existsByBothUsers(anyLong(), anyLong());
        verify(friendshipRepository, never()).existsByBothUserIds(anyLong(), anyLong());
        //the exists method are never called because we arrange the method to throw an exception
        // so the normal flow is interrupted
    }

    @Test
    void testCreateFriendship_FriendshipAlreadyExists(){
        //Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        when(userRepository.existsByBothUsers(userId1, userId2)).thenReturn(true);
        when(friendshipRepository.existsByBothUserIds(userId1, userId2)).thenReturn(true);
        //Act and assert
        assertThrows(ExistingFriendshipException.class, ()->userService.createFriendship(userId1, userId2)); //using lambda instead of ceating another method to execute
        verify(friendshipRepository, times(1)).existsByBothUserIds(userId1, userId2);
    }

    @Test
    void testCreateFriendship_UserNotFound(){
        //Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        when (userRepository.existsByBothUsers(userId1, userId2)).thenReturn(false);
        //Act and Assert
        assertThrows(UserNotFoundException.class, ()->userService.createFriendship(userId1, userId2));
        verify(friendshipRepository, never()).existsByBothUserIds(anyLong(), anyLong());
    }



    @Test
    void testExistsById() {
    }

    @Test
    void testGetAllFriendshipsByUserId() {

    }




    @Test
    void testDeleteFriendship() {

    }

}
