package net.pavlenko.networking.parameter.resolver;

import org.apache.commons.cli.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParametersResolver {
    public static Map<SimpleParameter, String> mapParameters(List<SimpleParameter> parameters, String [] args) throws ParseException {
        final CommandLine commandLine = createCommandLine(parameters, args);

        final Map<SimpleParameter, String> params = new LinkedHashMap<SimpleParameter, String>();
        for (SimpleParameter parameter : parameters) {
            String val = commandLine.getOptionValue(parameter.getLongOpt());
            if (val == null) {
                val = parameter.getDefaultValue();
            }

            params.put(parameter, val);
        }

        return params;
    }

    private static CommandLine createCommandLine(List<SimpleParameter> parameters, String [] args) throws ParseException {
        final Options posixOptions = new Options();
        for (SimpleParameter parameter : parameters) {
            final Option option = new Option(parameter.getOpt(), parameter.getLongOpt(), true, parameter.getLongOpt());
            option.setArgs(1);
            option.setOptionalArg(true);
            option.setArgName(parameter.getLongOpt() + " ");

            posixOptions.addOption(option);
        }

        final CommandLineParser cmdLinePosixParser = new PosixParser();
        return cmdLinePosixParser.parse(posixOptions, args);
    }
}
