package net.pavlenko.networking.server.nio.future;

import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import net.pavlenko.networking.server.excetion.ServerRuntimeException;
import net.pavlenko.networking.server.nio.ClientRejectExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.*;

import static net.pavlenko.networking.server.parameter.Parameter.*;

public class FutureNioServer {
    private static final Logger logger = LoggerFactory.getLogger(FutureNioServer.class);

    private AsynchronousServerSocketChannel serverSocketChannel;
    private ThreadPoolExecutor threadPoolExecutor;

    public FutureNioServer(Map<SimpleParameter, String> params) {
        final int port = Integer.parseInt(params.get(PORT));

        try {
            serverSocketChannel = AsynchronousServerSocketChannel.open();

            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                serverSocketChannel.bind(new InetSocketAddress(port));
            } else {
                logger.warn("Unable to start server");
                System.out.println("Unable to start server");
            }


            final Integer threads = Integer.parseInt(params.get(THREADS));
            threadPoolExecutor = new ThreadPoolExecutor(
                    threads,
                    threads,
                    1,
                    TimeUnit.MILLISECONDS,
                    new SynchronousQueue<Runnable>(),
                    new ClientRejectExecutionHandler()
            );
            threadPoolExecutor.allowCoreThreadTimeOut(true);

        } catch (IOException exc) {
            logger.error("Unable to start server", exc);
            throw new ServerRuntimeException("Unable to start server", exc);
        }
    }

    public void start() throws IOException, ExecutionException, InterruptedException {
        System.out.println("Waiting for connections ...");
        while (true ) {
            final Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = serverSocketChannel.accept();
            final AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get();
            threadPoolExecutor.execute(new FutureClientHandler(asynchronousSocketChannel));
        }
    }
}