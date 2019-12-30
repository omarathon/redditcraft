package dev.omarathon.redditcraft.data.engines.presets.sql.connection;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SQL {
    private Connection connection;
    private String accountTableName;
    private String authTableName;
    private HikariDataSource hikariDataSource;

    public SQL(SQLConfiguration sqlConfiguration) throws SQLException {
        setup(sqlConfiguration);
        String tablePrefix = sqlConfiguration.getTablePrefix();
        accountTableName = tablePrefix + "$" + "ACCOUNTS";
        authTableName = tablePrefix + "$" + "AUTH";
    }

    private void setup(SQLConfiguration configuration) throws SQLException {
        synchronized (this) {
            if (getConnection() != null && !getConnection().isClosed()) {
                return;
            }
            hikariDataSource.setMaximumPoolSize(10);
            hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikariDataSource.addDataSourceProperty("serverName", configuration.getHost());
            hikariDataSource.addDataSourceProperty("port", configuration.getPort());
            hikariDataSource.addDataSourceProperty("databaseName", configuration.getDatabase());
            hikariDataSource.addDataSourceProperty("user", configuration.getUsername());
            hikariDataSource.addDataSourceProperty("password", configuration.getPassword());
            setConnection(this.hikariDataSource.getConnection());
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
