package ml.echelon133.user;

public class UsernameAlreadyTakenException extends Exception {

    public UsernameAlreadyTakenException(String msg) {
        super(msg);
    }
}
