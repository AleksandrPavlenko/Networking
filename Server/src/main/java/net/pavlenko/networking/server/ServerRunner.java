package net.pavlenko.networking.server;

import net.pavlenko.networking.parameter.resolver.ParametersResolver;
import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import net.pavlenko.networking.server.nio.completion.handler.NioServer;
import net.pavlenko.networking.server.nio.future.FutureNioServer;
import net.pavlenko.networking.server.parameter.Parameter;
import net.pavlenko.networking.server.socket.SocketServer;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static net.pavlenko.networking.server.parameter.Parameter.*;

public class ServerRunner {
    private static final Logger logger = LoggerFactory.getLogger(ServerRunner.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final List<SimpleParameter> parameters = new ArrayList<SimpleParameter>(Arrays.asList(Parameter.values()));
        final Map<SimpleParameter, String> params;
        try {
            params = ParametersResolver.mapParameters(parameters, args);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        final String serverType = params.get(SERVER);

        switch (serverType) {
            case "nio":
                new NioServer(params).start();
                break;
            case "fnio":
                new FutureNioServer(params).start();
                break;
            case "socket":
                new SocketServer(params).start();
                break;

            default:
                throw new RuntimeException();
        }
    }
}