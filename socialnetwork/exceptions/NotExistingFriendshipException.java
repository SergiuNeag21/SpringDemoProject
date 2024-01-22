package socialnetwork.exceptions;

public class NotExistingFriendshipException extends RuntimeException{
    public NotExistingFriendshipException(String message){
        super(message);
    }
    
}