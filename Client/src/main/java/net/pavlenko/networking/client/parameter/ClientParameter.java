package net.pavlenko.networking.client.parameter;

import net.pavlenko.networking.parameter.resolver.SimpleParameter;

public enum ClientParameter implements SimpleParameter {
    ATTEMPT("a", "attempt", "10"),
    CLIENT("c", "client", "socket"),
    HOST("h", "host", "127.0.0.1"),
    PORT("p", "port", "9000"),
    REQUEST("r", "request", "default request");

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

    private ClientParameter(String opt, String longOpt, String defaultValue) {
        this.opt = opt;
        this.longOpt = longOpt;
        this.defaultValue = defaultValue;
    }
}
