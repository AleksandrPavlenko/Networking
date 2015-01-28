package net.pavlenko.networking.server.nio.future;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutionException;

public class FutureClientHandler implements Runnable {
    private AsynchronousSocketChannel asynchronousSocketChannel;

    public FutureClientHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @Override
    public void run() {
        final Charset charset = Charset.defaultCharset();
        final CharsetDecoder decoder = charset.newDecoder();
        final StopWatch stopWatch = new Slf4JStopWatch("client");
        try {
            final String host = asynchronousSocketChannel.getRemoteAddress().toString();

            System.out.println("Incoming connection from: " + host);
            final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

            while (asynchronousSocketChannel.read(buffer).get() != -1) {
                buffer.flip();
                System.out.println(decoder.decode(buffer));
                buffer.flip();
                asynchronousSocketChannel.write(buffer).get();
                if (buffer.hasRemaining()) {
                    buffer.compact();
                } else {
                    buffer.clear();
                }
            }
            asynchronousSocketChannel.close();
            System.out.println(host + " was successfully served!");
            stopWatch.stop();

        } catch (IOException | InterruptedException | ExecutionException ex) {
            System.err.println(ex);
        }
    }
}
