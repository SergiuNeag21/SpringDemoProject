package socialnetwork.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.exceptions.ExistingFriendshipException;
import socialnetwork.exceptions.NotExistingFriendshipException;
import socialnetwork.exceptions.SameUserFriendshipException;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.exceptions.WrongUserException;
import socialnetwork.service.UserService;

@Slf4j
@RestController
@RequestMapping(value = "/api/users", produces = "application/json")  
public class UserController {

    @Autowired 
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/getMessage")
    public ResponseEntity<String> saySomething(){
        return ResponseEntity.ok("Hi!");
    }

    @GetMapping(value = "/getAllUsers")
    // @RolesAllowed({Role.ADMIN.toString()});
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("This is a debug message.");
        log.info("This is an info message.");
        log.warn("This is a warning message.");
        log.error("This is an error message.");
        
        log.info("Before calling userService.getAllUsers()");
        List<User> users = userService.getAllUsers();
        log.info("After calling userService.getAllUsers()");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {

        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);

    }

    @DeleteMapping(value = "/deleteUser/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        Optional<User> userOptional = userService.getUserByID(userId);
        if (userOptional.isPresent()){
            userService.deleteUser(userId);
            return new ResponseEntity<>(userOptional, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/getUserById/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId){ 
        Optional<User> userOptional = userService.getUserByID(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping(value = "/updateUser/{userId}", consumes = "application/json")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Valid @RequestBody User user){
        Optional<User> userOptional = userService.getUserByID(userId);
        if (userOptional.isPresent()) {
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/getUserByEmail/{email}")
    public ResponseEntity<Object> getUserByEmail(@PathVariable @NotBlank @Email String email){
        Optional<User> optionalUser = userService.getUserByEmail(email);

        if (optionalUser.isPresent()){
            return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User with this email not found", HttpStatus.NOT_FOUND);
        }
            
    }

    @DeleteMapping(value = "/deleteUserWithError/{userId}")
    public ResponseEntity<Object> deleteUserWithError(@PathVariable Long userId){
        try {
            Optional<User> deletedUser = userService.deleteUserByIdWithError(userId);
            return new ResponseEntity<>("User deleted succesfully: " + deletedUser, HttpStatus.OK);
        } catch (WrongUserException ex) {
            log.warn("Warn message ", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }   
    }

    @PostMapping("/{userId1}/createFriendship/{userId2}")
    public ResponseEntity<Object> createFriendship(@PathVariable Long userId1, @PathVariable Long userId2){
        try{
            Optional<Friendship> createdFriendship = userService.createFriendship(userId1, userId2);
            return new ResponseEntity<>("Friendship created succesfully" + createdFriendship, HttpStatus.OK);
        }
        catch (SameUserFriendshipException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);  //400
        }
        catch (UserNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);    //404
        }
        catch (ExistingFriendshipException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId1}/deleteFriendship/{userId2}")
    public ResponseEntity<Object> deleteFriendshipById(@PathVariable Long userId1, @PathVariable Long userId2){
        try {
            userService.deleteFriendship(userId1, userId2);
            return new ResponseEntity<>("Friendship deleted successfully", HttpStatus.OK);
        }
        catch (UserNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);    //404
        }
        catch (NotExistingFriendshipException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/{userId}/getAllFriendshipsByUserId")
    public ResponseEntity<Object> getAllFriendshipsByUserId(@PathVariable Long userId){
        try {
            return new ResponseEntity<>(userService.getAllFriendshipsByUserId(userId), HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}