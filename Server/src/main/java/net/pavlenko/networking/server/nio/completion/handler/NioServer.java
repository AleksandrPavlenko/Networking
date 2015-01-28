package net.pavlenko.networking.server.nio.completion.handler;

import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import net.pavlenko.networking.server.excetion.ServerRuntimeException;
import net.pavlenko.networking.server.nio.ClientRejectExecutionHandler;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static net.pavlenko.networking.server.parameter.Parameter.PORT;
import static net.pavlenko.networking.server.parameter.Parameter.THREADS;

public class NioServer {
    private static final Logger logger = LoggerFactory.getLogger(NioServer.class);

    private AsynchronousServerSocketChannel serverSocketChannel;
    private static List<UUID> activeClients = new ArrayList<UUID>();
    private static StopWatch stopWatch = new Slf4JStopWatch();

    public NioServer(Map<SimpleParameter, String> params) {
        final Integer port = Integer.parseInt(params.get(PORT));
        final Integer threads = Integer.parseInt(params.get(THREADS));

        try {
            final ExecutorService executorService = Executors.newCachedThreadPool();
            final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    threads,
                    threads,
                    1,
                    TimeUnit.MILLISECONDS,
                    new SynchronousQueue<Runnable>(),
                    new ClientRejectExecutionHandler()
            );
            threadPoolExecutor.allowCoreThreadTimeOut(true);

            final AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(executorService);
            serverSocketChannel = AsynchronousServerSocketChannel.open(group);

            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                serverSocketChannel.bind(new InetSocketAddress(port));
            } else {
                logger.warn("Unable to start server");
                System.out.println("Unable to start server");
            }

        } catch (IOException exc) {
            logger.error("Unable to start server", exc);
            throw new ServerRuntimeException("Unable to start server", exc);
        }
    }

    public void start() {
        System.out.println("Waiting for connections ...");
        serverSocketChannel.accept(null, new ServerCompletionHandler(serverSocketChannel));

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addClient(UUID clientId) {
        activeClients.add(clientId);
        stopWatch.start(clientId.toString());

        final String msg = String.format("New client connected. [ClientId: %s]", clientId);
        logger.debug(msg);
        System.out.println(msg);
    }

    public static synchronized void removeClient(UUID clientId) {
        stopWatch.stop(clientId.toString());
        activeClients.remove(clientId);


        final String msg = String.format("Client disconnected. [ClientId: %s]", clientId);
        logger.debug(msg);
        System.out.println(msg);
    }
}