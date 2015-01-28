package net.pavlenko.networking.client.socket;


import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;

import static net.pavlenko.networking.client.parameter.ClientParameter.*;

/**
 * Socket client based on messages
 */
public class MSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    public void start(Map<SimpleParameter, String> params) throws IOException, InterruptedException {
        final String host = params.get(HOST);
        final Integer port = Integer.parseInt(params.get(PORT));

        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        Socket socket = null;

        try {
            socket = new Socket(host, port);
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());

            handleRequest(params, in, out);
        } catch (UnknownHostException exc) {
            final String msg = String.format("Unable connect, host %s does not exist", host);
            System.out.println(msg);
            logger.error(msg, exc);
            System.exit(1);
            return;
        } catch (IOException exc) {
            final String msg = String.format("Unable connect to server");
            System.out.println(msg);
            logger.error(msg, exc);
            System.exit(1);
            return;
        } finally {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException exc) {
                logger.warn("Unable to close client socket", exc);
            }
        }

        System.exit(0);
    }

    private void handleRequest(Map<SimpleParameter, String> params, BufferedInputStream in, BufferedOutputStream out) throws IOException {
        final String request = params.get(REQUEST);
        final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

        for (int i = 0; i < Integer.parseInt(params.get(ATTEMPT)); ++i) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(4 + request.getBytes().length)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putInt(request.length())
                    .put(request.getBytes());
            out.write(byteBuffer.array());
            out.flush();

            final byte [] msgSize = new byte[4];
            in.read(msgSize);

            final int responseSize = ByteBuffer.wrap(msgSize).getInt();
            final byte [] response = new byte[responseSize];
            in.read(response);
            System.out.println(decoder.decode(ByteBuffer.wrap(response)));
        }
    }
}
