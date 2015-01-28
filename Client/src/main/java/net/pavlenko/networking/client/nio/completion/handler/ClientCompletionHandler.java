package net.pavlenko.networking.client.nio.completion.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ClientCompletionHandler implements CompletionHandler<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(ClientCompletionHandler.class);

    private static final Integer INTEGER_SIZE = 4;

    private AsynchronousSocketChannel socketChannel;
    private Integer attempts;
    private String request;

    public ClientCompletionHandler(AsynchronousSocketChannel socketChannel, Integer attempts, String request) {
        this.socketChannel = socketChannel;
        this.attempts = attempts;
        this.request = request;
    }

    @Override
    public void completed(Void result, Void attachment) {
        try {
            System.out.println("Successfully connected at: " + socketChannel.getRemoteAddress());
            for (int i = 0; i < attempts; i++) {
                final String msg = (request);
                final int msgLength = msg.getBytes().length;
                ByteBuffer msgBuffer = ByteBuffer.allocate(INTEGER_SIZE+msgLength)
                        .putInt(msgLength)
                        .put(msg.getBytes());
                msgBuffer.flip();
                socketChannel.write(msgBuffer).get();

                final ByteBuffer msgLengthBuffer = ByteBuffer.allocate(INTEGER_SIZE);
                socketChannel.read(msgLengthBuffer).get();

                msgLengthBuffer.rewind();
                final int bufferSize = msgLengthBuffer.getInt();
                msgBuffer = ByteBuffer.allocate(bufferSize);
                socketChannel.read(msgBuffer).get();

                final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
                msgBuffer.flip();
                System.out.println(decoder.decode(msgBuffer));

                msgBuffer.clear();
                msgLengthBuffer.clear();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            try {
                socketChannel.close();
                synchronized (socketChannel) {
                    socketChannel.notify();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        throw new UnsupportedOperationException("Connection cannot be established!");
    }
}
