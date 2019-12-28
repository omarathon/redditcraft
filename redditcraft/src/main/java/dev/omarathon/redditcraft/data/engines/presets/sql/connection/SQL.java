package dev.omarathon.redditcraft.data.engines.presets.sql.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQL {
    private Connection connection;
    private String accountTableName;
    private String authTableName;

    public SQL(SQLConfiguration sqlConfiguration) throws SQLException, ClassNotFoundException {
        setup(sqlConfiguration);
        String tablePrefix = sqlConfiguration.getTablePrefix();
        accountTableName = tablePrefix + "$" + "ACCOUNTS";
        authTableName = tablePrefix + "$" + "AUTH";
    }

    private void setup(SQLConfiguration configuration) throws SQLException, ClassNotFoundException {
        synchronized (this) {
            if (getConnection() != null && !getConnection().isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            setConnection(DriverManager.getConnection("jdbc:mysql://" + configuration.getHost() + ":" + configuration.getPort() + "/" + configuration.getDatabase(), configuration.getUsername(), configuration.getPassword()));
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getAccountTableName() {
        return accountTableName;
    }

    public String getAuthTableName() {
        return authTableName;
    }

    public void close() throws SQLException {
        if (!connection.isClosed()) connection.close();
    }
}
