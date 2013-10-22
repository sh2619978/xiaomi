package client;

public class MiClientException extends Exception {

    public MiClientException() {
        super();
    }

    public MiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public MiClientException(String message) {
        super(message);
    }

    public MiClientException(Throwable cause) {
        super(cause);
    }
    
}
