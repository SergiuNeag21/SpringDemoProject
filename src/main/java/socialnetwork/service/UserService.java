package socialnetwork.service;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.exceptions.ExistingFriendshipException;
import socialnetwork.exceptions.NotExistingFriendshipException;
import socialnetwork.exceptions.SameUserFriendshipException;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.exceptions.WrongUserException;
import socialnetwork.repository.FriendshipRepository;
import socialnetwork.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public UserService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // using optional to handle the scenario where the user is not found
    public Optional<User> getUserByID(Long userID){
        return userRepository.findById(userID);
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> deleteUserByIdWithError(Long userId){
        if (userId % 2 == 0){
            throw new WrongUserException("Cannot delete a user with even id");
        }
        else{
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()){
                userRepository.deleteById(userId);
                return optionalUser;
            }
            else throw new UserNotFoundException("Cannot find this user in databse");
        }
    }

    public boolean existsById(Long userId){
        return userRepository.existsById(userId);
    }

    public Optional<Friendship> createFriendship(Long userId1, Long userId2){
        if (userId1.equals(userId2)){
            throw new SameUserFriendshipException("Cannot create friendship with the same user");
        }
        if (!userRepository.existsByBothUsers(userId1, userId2)){
            throw new UserNotFoundException("User with this id doesn`t exist.");
        }
        // verify if friendship doesn`t exist
        else{
            if (friendshipRepository.existsByBothUserIds(userId1, userId2))
                throw new ExistingFriendshipException("This friendship already exists.");
        }
        Friendship friendship = new Friendship(userId1, userId2);
        friendshipRepository.save(friendship);
        return Optional.of(friendship);
    }

    // ? should i delete after friendship id or user id?\
    @Transactional
    public void deleteFriendship(Long userId1, Long userId2){
        if (!userRepository.existsByBothUsers(userId1, userId2)){
            throw new UserNotFoundException("User with this id doesn`t exist.");
        }
        // verify if friendship exists
        else{
            if (friendshipRepository.existsByUserId1AndUserId2(userId1, userId2)){
                friendshipRepository.deleteByUserId1AndUserId2(userId1, userId2);
            }
            else if (friendshipRepository.existsByUserId1AndUserId2(userId2, userId1)){
                friendshipRepository.deleteByUserId1AndUserId2(userId2, userId1);
            }
            else {
                throw new NotExistingFriendshipException("This friendship doesn`t exist.");
            }
        }
    }


    public List<Friendship> getAllFriendshipsByUserId(Long userId){
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Cannot find the user with this id.");
        }
        return friendshipRepository.getAllFriendshipsByUserId(userId);
    } 

}
