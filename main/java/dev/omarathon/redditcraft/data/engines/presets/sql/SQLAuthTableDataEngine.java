package dev.omarathon.redditcraft.data.engines.presets.sql;

import dev.omarathon.redditcraft.data.engines.AuthTableDataEngine;
import dev.omarathon.redditcraft.data.engines.presets.sql.connection.AccountFields;
import dev.omarathon.redditcraft.data.engines.presets.sql.connection.AuthFields;
import dev.omarathon.redditcraft.data.engines.presets.sql.connection.SQL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class SQLAuthTableDataEngine implements AuthTableDataEngine {
    private SQL sql;
    private Connection connection;

    public SQLAuthTableDataEngine(SQL sql) {
        this.sql = sql;
        this.connection = sql.getConnection();
    }

    @Nullable
    @Override
    public LocalDateTime getExpiryTime(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AuthFields.EXPIRY_TIME + " FROM " + sql.getAuthTableName() + " WHERE " + AuthFields.UUID + "=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            LocalDateTime result = null;
            if (resultSet.next()) {
                result = resultSet.getTimestamp(AuthFields.EXPIRY_TIME).toLocalDateTime();
            }
            resultSet.close();
            preparedStatement.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsToken(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM " + sql.getAuthTableName() + " WHERE " + AuthFields.UUID + "=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean result = false;
            if (resultSet.next()) {
                result = true;
            }
            resultSet.close();
            preparedStatement.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addToken(@NotNull UUID uuid, @NotNull LocalDateTime expiry) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + sql.getAuthTableName() + " VALUES (?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(expiry));
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateExpiryTime(@NotNull UUID uuid, @NotNull LocalDateTime newExpiry) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + sql.getAuthTableName() + " SET " + AuthFields.EXPIRY_TIME + "=? WHERE " + AuthFields.UUID + "=?");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(newExpiry));
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllExpiredTokens() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + sql.getAuthTableName() + " WHERE " + AuthFields.EXPIRY_TIME + " <= CURRENT_TIMESTAMP ");
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeToken(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + sql.getAuthTableName() + " WHERE " + AuthFields.UUID + "=?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllTokens() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE " + sql.getAuthTableName());
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE " + sql.getAuthTableName() + " (" + AuthFields.UUID + " CHAR(36) NOT NULL, " + AuthFields.EXPIRY_TIME + " TIMESTAMP NOT NULL, PRIMARY KEY (" + AuthFields.UUID + "), FOREIGN KEY (" + AuthFields.UUID + ") REFERENCES " + sql.getAccountTableName() + "(" + AccountFields.UUID + "))");
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE " + sql.getAuthTableName());
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists() {
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet table = databaseMetaData.getTables(null, null, sql.getAuthTableName(), null);
            boolean result = false;
            if (table.next()) {
                result = true;
            }
            table.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
