package net.pavlenko.networking.server.nio.completion.handler;

import org.perf4j.StopWatch;
import org.perf4j.javalog.AsyncCoalescingHandler;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutionException;

public class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
    private static final Logger logger = LoggerFactory.getLogger(AsyncCoalescingHandler.class);

    private AsynchronousServerSocketChannel serverSocketChannel;

    public ServerCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void completed(final AsynchronousSocketChannel socketChannel, Void attachment) {
        serverSocketChannel.accept(null, this);
        final StopWatch stopWatch = new Slf4JStopWatch("client");
        final ByteBuffer msgSizeBuffer = ByteBuffer.allocate(4);
        socketChannel.read(msgSizeBuffer, null, new ReadCompletionHandler(socketChannel, msgSizeBuffer, stopWatch));
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        logger.warn("Unable to accept client connection", exc);
    }

    private class ReadCompletionHandler implements CompletionHandler<Integer, Void> {
        private AsynchronousSocketChannel socketChannel;
        private ByteBuffer msgSizeBuffer;
        private StopWatch stopWatch;

        private final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

        private ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer msgSizeBuffer, StopWatch stopWatch) {
            this.socketChannel = socketChannel;
            this.msgSizeBuffer = msgSizeBuffer;
            this.stopWatch = stopWatch;
        }

        @Override
        public void completed(Integer result, Void attachment) {
            if (result == -1) {
                stopWatch.stop("client");
                logger.debug("Connection finished");
            }

            if (result != 4) {
                return;
            }

            msgSizeBuffer.rewind();
            final int msgSize = msgSizeBuffer.getInt();
            final ByteBuffer msgBuffer = ByteBuffer.allocate(msgSize);

            try {
                socketChannel.read(msgBuffer).get();
                msgSizeBuffer.flip();
                socketChannel.write(msgSizeBuffer).get();
                msgBuffer.flip();
                socketChannel.write(msgBuffer).get();
                msgBuffer.flip();
                logger.debug(decoder.decode(msgBuffer).toString());
                msgBuffer.clear();
            } catch (InterruptedException | ExecutionException | CharacterCodingException exc) {
                exc.printStackTrace();
            }

            msgSizeBuffer.clear();

            socketChannel.read(msgSizeBuffer, null, this);
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            logger.warn("Failed to read message");
        }
    }
}
