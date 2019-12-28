package dev.omarathon.redditcraft.data.engines.presets.sql.connection;

public class SQLConfiguration {
    private String host, database, tablePrefix, username, password;
    private int port;

    public SQLConfiguration(String host, int port, String database, String tablePrefix, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.tablePrefix = tablePrefix;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }
}
