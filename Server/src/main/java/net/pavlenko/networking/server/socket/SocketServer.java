package net.pavlenko.networking.server.socket;

import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import net.pavlenko.networking.server.parameter.Parameter;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private static final long KEEP_ALIVE_TIME = 1;

    private Integer port;
    private Integer maxThreads;

    private static List<UUID> activeClients = new ArrayList<UUID>();

    public SocketServer(Map<SimpleParameter, String> params ) {
        port = Integer.parseInt(params.get(Parameter.PORT));
        maxThreads = Integer.parseInt(params.get(Parameter.THREADS));
    }

    public void start() {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                new ClientRejectExecutionHandler()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);

        try {
            final ServerSocket serverSocket = new ServerSocket(port, maxThreads);

            final String msg = String.format("Server started. [Port: %s; Max connections: %s]", port, maxThreads);
            logger.info(msg);
            System.out.println(msg);

            while (true) {
                final Socket socket = serverSocket.accept();
                final StopWatch stopWatch = new Slf4JStopWatch();
                threadPoolExecutor.execute(new MClientConnection(socket, stopWatch));
            }
        } catch (IOException exc) {
            logger.error(String.format("Unable to start server. [Port: %s]", port), exc);
        } finally {
            threadPoolExecutor.shutdown();
        }
    }

    public static synchronized void addClient(UUID clientId) {
        activeClients.add(clientId);

        final String msg = String.format("New client connected. [ClientId: %s]", clientId);
        logger.debug(msg);
        System.out.println(msg);
    }

    public static synchronized void removeClient(UUID clientId) {
        activeClients.remove(clientId);

        final String msg = String.format("Client disconnected. [ClientId: %s]", clientId);
        logger.debug(msg);
        System.out.println(msg);
    }
}