package net.pavlenko.networking.server.socket;

import net.pavlenko.networking.server.socket.exception.SocketServerRuntimeException;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

public class ClientConnection implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);

    private Socket socket;
    private UUID clientId;
    private StopWatch stopWatch;

    public ClientConnection(Socket socket, StopWatch stopWatch) {
        this.socket = socket;
        this.clientId = UUID.randomUUID();
        this.stopWatch = stopWatch;
    }

    @Override
    public void run() {
        try {
            SocketServer.addClient(clientId);
            handleRequests(socket, clientId);
        } catch (SocketException exc) {
            logger.warn("Socket connection was closed");
        } catch (IOException exc) {
            final String msg = String.format("Unable to handle request");
            logger.error(msg, exc);
            throw new SocketServerRuntimeException(msg, exc);
        }
    }

    private void handleRequests(Socket socket, UUID clientId) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
        } finally {
            SocketServer.removeClient(clientId);
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            stopWatch.stop("client");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public UUID getClientId() {
        return clientId;
    }
}