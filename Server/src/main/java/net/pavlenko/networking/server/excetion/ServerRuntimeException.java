package net.pavlenko.networking.server.excetion;

public class ServerRuntimeException extends RuntimeException {
    public ServerRuntimeException(String message) {
        super(message);
    }

    public ServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
