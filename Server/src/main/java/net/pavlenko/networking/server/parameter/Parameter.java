package net.pavlenko.networking.server.parameter;

import net.pavlenko.networking.parameter.resolver.SimpleParameter;

public enum Parameter implements SimpleParameter {
    PORT("p", "port", "9000"),
    THREADS("t", "threads", "5"),
    SERVER("s", "server", "socket");

    private String opt;
    private String longOpt;
    private String defaultValue;

    @Override
    public String getOpt() {
        return opt;
    }

    @Override
    public String getLongOpt() {
        return longOpt;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    private Parameter(String opt, String longOpt, String defaultValue) {
        this.opt = opt;
        this.longOpt = longOpt;
        this.defaultValue = defaultValue;
    }
}
