package net.pavlenko.networking.client;

import net.pavlenko.networking.client.nio.completion.handler.NioClient;
import net.pavlenko.networking.client.nio.future.FutureNioClient;
import net.pavlenko.networking.client.parameter.ClientParameter;
import net.pavlenko.networking.client.socket.MSocketClient;
import net.pavlenko.networking.client.socket.SocketClient;
import net.pavlenko.networking.parameter.resolver.ParametersResolver;
import net.pavlenko.networking.parameter.resolver.SimpleParameter;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static net.pavlenko.networking.client.parameter.ClientParameter.*;

public class ClientRunner {
    private static final Logger logger = LoggerFactory.getLogger(ClientRunner.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        final List<SimpleParameter> parameters = new ArrayList<SimpleParameter>(Arrays.asList(ClientParameter.values()));
        final Map<SimpleParameter, String> params;
        try {
            params = ParametersResolver.mapParameters(parameters, args);
        } catch (ParseException exc) {
            logger.error("Unable to parse client parameters.", exc);
            throw new RuntimeException("Unable to parse client parameters.", exc);
        }

        final String clientType = params.get(CLIENT).toLowerCase();
        switch (clientType) {
            case "socket":
                new MSocketClient().start(params);
                break;
            case "nio":
                new NioClient().start(params);
                break;
            case "fnio":
                new FutureNioClient().start(params);
                break;

            default:
                final String msg = String.format("Unrecognized client type: %s", clientType);
                logger.error(msg);
                System.out.println(msg);
                throw new RuntimeException(msg);
        }
    }
}