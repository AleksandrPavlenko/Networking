package net.pavlenko.networking.server.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientRejectExecutionHandler implements RejectedExecutionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientRejectExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.warn("Client was rejected");
    }
}
