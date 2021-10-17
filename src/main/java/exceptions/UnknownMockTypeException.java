package exceptions;

public class UnknownMockTypeException extends RuntimeException{
    public UnknownMockTypeException(String errorMessage) {
        super(errorMessage);
    }
}
