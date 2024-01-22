package socialnetwork.exceptions;

public class SameUserFriendshipException extends RuntimeException{
    public SameUserFriendshipException(String message){
        super(message);
    }
}
