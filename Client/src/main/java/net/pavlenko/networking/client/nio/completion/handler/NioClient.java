package net.pavlenko.networking.client.nio.completion.handler;

import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static net.pavlenko.networking.client.parameter.ClientParameter.*;
public class NioClient {
    private static final Logger logger = LoggerFactory.getLogger(NioClient.class);

    public void start(Map<SimpleParameter, String> params) {

        final Integer port = Integer.parseInt(params.get(PORT));
        final Integer attempts = Integer.parseInt(params.get(ATTEMPT));
        final String host = params.get(HOST);
        final String request = params.get(REQUEST);

        try {
            final AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
            if (socketChannel.isOpen()) {
//                socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 8096);
//                socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 100000 * 8096);
                socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

                socketChannel.connect(new InetSocketAddress(host, port), null, new ClientCompletionHandler(socketChannel, attempts, request));
            } else {
                System.out.println("The asynchronous socket channel cannot be opened!");
            }

            synchronized (socketChannel) {
                socketChannel.wait();
            }

        } catch (IOException exc) {
            System.err.println(exc);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
