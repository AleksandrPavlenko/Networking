package net.pavlenko.networking.server.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientRejectExecutionHandler implements RejectedExecutionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientRejectExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        final ClientConnection clientConnection = (ClientConnection) r;
        assert (clientConnection.getSocket() != null);
        PrintWriter out = null;

        try {
            out = new PrintWriter(clientConnection.getSocket().getOutputStream(), true);
            out.write("Too many clients, connection will be rejected");
            out.flush();

            final String msg = String.format("Too many clients, connection will be rejected. [ClientId: %s]",
                    clientConnection.getClientId());
            System.out.println(msg);
            logger.info(msg);
        } catch (IOException exc) {
            final String msg = "Unable to send rejection message to client";
            logger.warn(msg, exc);
        } finally {
            if (out != null) {
                out.close();
            }

            try {
                clientConnection.getSocket().close();
            } catch (IOException exc) {
                logger.warn("Unable to close client socket", exc);
            }
        }
    }
}
