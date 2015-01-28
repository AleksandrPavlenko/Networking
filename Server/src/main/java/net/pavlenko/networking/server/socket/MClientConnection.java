package net.pavlenko.networking.server.socket;

import net.pavlenko.networking.server.socket.exception.SocketServerRuntimeException;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.UUID;

public class MClientConnection implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);

    private Socket socket;
    private UUID clientId;
    private StopWatch stopWatch;

    public MClientConnection(Socket socket, StopWatch stopWatch) {
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
        final BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
        final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

        try {
            while (true) {
                byte [] msgSizeBuffer = new byte[4];
                in.read(msgSizeBuffer);
                final ByteBuffer buffer = ByteBuffer.wrap(msgSizeBuffer)
                        .order(ByteOrder.BIG_ENDIAN);
                int msgSize = buffer.getInt();

                final byte [] bytes = new byte[msgSize];
                in.read(bytes);
                final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
                System.out.println(decoder.decode(byteBuffer));

                out.write(msgSizeBuffer);
                out.write(bytes);
                out.flush();

                buffer.clear();
                byteBuffer.clear();
            }
        } finally {
            in.close();
            out.close();
            socket.close();
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