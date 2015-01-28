package net.pavlenko.networking.client.socket;


import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Simple echo Socket client.
 */
import static net.pavlenko.networking.client.parameter.ClientParameter.*;
public class SocketClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    public void start(Map<SimpleParameter, String> params) throws IOException, InterruptedException {
        final String host = params.get(HOST);
        final Integer port = Integer.parseInt(params.get(PORT));

        BufferedReader in = null;
        PrintWriter out = null;
        Socket socket = null;

        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

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

    private void handleRequest(Map<SimpleParameter, String> params, BufferedReader in, PrintWriter out) throws IOException {
        final String request = params.get(REQUEST);

        for (int i = 0; i < Integer.parseInt(params.get(ATTEMPT)); ++i) {
            String inputLine;
            if (in.ready() && (inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }


            out.println(request.length());

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);

                if (!in.ready()) {
                    break;
                }
            }
        }
    }
}
