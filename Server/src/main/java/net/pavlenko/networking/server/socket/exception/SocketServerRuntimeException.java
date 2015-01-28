package net.pavlenko.networking.server.socket.exception;

public class SocketServerRuntimeException extends RuntimeException {
    public SocketServerRuntimeException(String message) {
        super(message);
    }

    public SocketServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
