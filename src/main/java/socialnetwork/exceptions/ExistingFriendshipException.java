package socialnetwork.exceptions;

public class ExistingFriendshipException extends RuntimeException{
    public ExistingFriendshipException(String message){
        super(message);
    }
    
}
