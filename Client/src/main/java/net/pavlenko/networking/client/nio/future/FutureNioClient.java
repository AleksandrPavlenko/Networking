package net.pavlenko.networking.client.nio.future;


import net.pavlenko.networking.parameter.resolver.SimpleParameter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static net.pavlenko.networking.client.parameter.ClientParameter.*;

public class FutureNioClient {
    public void start(Map<SimpleParameter, String> params) {
        final String host = params.get(HOST);
        final Integer port = Integer.parseInt(params.get(PORT));
        final String request = params.get(REQUEST);
        final Integer attempts = Integer.parseInt(params.get(ATTEMPT));

        final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        final ByteBuffer helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
        final Charset charset = Charset.defaultCharset();
        final CharsetDecoder decoder = charset.newDecoder();

        try (AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open()) {
            if (asynchronousSocketChannel.isOpen()) {
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
                asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

                final Void connect = asynchronousSocketChannel.connect (new InetSocketAddress(host, port)).get();
                if (connect == null) {
                    System.out.println("Local address: " + asynchronousSocketChannel.getLocalAddress());
                    asynchronousSocketChannel.write(helloBuffer).get();
                    int count = 0;
                    while (asynchronousSocketChannel.read(buffer).get() != -1) {
                        buffer.flip();
                        final CharBuffer charBuffer = decoder.decode(buffer);
                        System.out.println(charBuffer.toString());
                        if (buffer.hasRemaining()) {
                            buffer.compact();
                        } else {
                            buffer.clear();
                        }
                        if (count == attempts) {
                            System.out.println(attempts+" was generated! Close the asynchronous socket channel!");
                            break;
                        } else {
                            final ByteBuffer randomBuffer = ByteBuffer.wrap((request+"_"+count).getBytes());
                            asynchronousSocketChannel.write(randomBuffer).get();
                            ++count;
                        }
                    }
                } else {
                    System.out.println("The connection cannot be established!");
                }
            } else {
                System.out.println("The asynchronous socket channel cannot be opened!");
            }
        } catch (IOException | InterruptedException | ExecutionException ex) {
            System.err.println(ex);
        }
    }
}
