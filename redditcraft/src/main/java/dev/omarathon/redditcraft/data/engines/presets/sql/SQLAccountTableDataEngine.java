package dev.omarathon.redditcraft.data.engines.presets.sql;

import dev.omarathon.redditcraft.data.endpoints.RedditUsernameAuthenticatedPair;
import dev.omarathon.redditcraft.data.engines.AccountTableDataEngine;
import dev.omarathon.redditcraft.data.engines.presets.sql.connection.AccountFields;
import dev.omarathon.redditcraft.data.engines.presets.sql.connection.SQL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLAccountTableDataEngine implements AccountTableDataEngine {
    private SQL sql;
    private Connection connection;

    public SQLAccountTableDataEngine(SQL sql) {
        this.sql = sql;
        this.connection = sql.getConnection();
    }

    @Nullable
    @Override
    public Boolean isAuthenticated(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AccountFields.AUTHENTICATED + " FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.UUID + "=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            Boolean result = null;
            if (resultSet.next()) {
                result = resultSet.getBoolean(AccountFields.AUTHENTICATED);
            }
            resultSet.close();
            preparedStatement.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public String getRedditUsername(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AccountFields.REDDIT_USERNAME + " FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.UUID + "=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            String result = null;
            if (resultSet.next()) {
                result = resultSet.getString(AccountFields.REDDIT_USERNAME);
            }
            resultSet.close();
            preparedStatement.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public RedditUsernameAuthenticatedPair getRedditUsernameAuthenticatedPair(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AccountFields.REDDIT_USERNAME + ", " + AccountFields.AUTHENTICATED + " FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.UUID + "=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            RedditUsernameAuthenticatedPair result = null;
            if (resultSet.next()) {
                String redditUsername = resultSet.getString(AccountFields.REDDIT_USERNAME);
                boolean authenticated = resultSet.getBoolean(AccountFields.AUTHENTICATED);
                result = new RedditUsernameAuthenticatedPair(redditUsername, authenticated);
            }
            resultSet.close();
            preparedStatement.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public Integer getFlair(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AccountFields.FLAIR + " FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.UUID + "=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            Integer result = null;
            if (resultSet.next()) {
                result = resultSet.getInt(AccountFields.FLAIR);
            }
            resultSet.close();
            preparedStatement.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Iterable<UUID> getAllUuidsWithRedditUsername(@NotNull String redditUsername) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AccountFields.UUID + " FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.REDDIT_USERNAME + "=?");
            preparedStatement.setString(1, redditUsername);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<UUID> uuids = new ArrayList<>(1);
            while (resultSet.next()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(resultSet.getString(AccountFields.UUID));
                }
                catch (IllegalArgumentException e) {
                    continue;
                }
                uuids.add(uuid);
            }
            resultSet.close();
            preparedStatement.close();
            return uuids;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Iterable<UUID> getAllAuthenticatedUuidsWithRedditUsername(@NotNull String redditUsername) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AccountFields.UUID + " FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.REDDIT_USERNAME + "=? AND " + AccountFields.AUTHENTICATED + "=?");
            preparedStatement.setString(1, redditUsername);
            preparedStatement.setBoolean(2, true);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<UUID> uuids = new ArrayList<>(1);
            while (resultSet.next()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(resultSet.getString(AccountFields.UUID));
                }
                catch (IllegalArgumentException e) {
                    continue;
                }
                uuids.add(uuid);
            }
            resultSet.close();
            preparedStatement.close();
            return uuids;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Iterable<UUID> getAllAuthenticatedUuids() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + AccountFields.UUID + " FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.AUTHENTICATED + "=?");
            preparedStatement.setBoolean(1, true);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<UUID> uuids = new ArrayList<>(1);
            while (resultSet.next()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(resultSet.getString(AccountFields.UUID));
                }
                catch (IllegalArgumentException e) {
                    continue;
                }
                uuids.add(uuid);
            }
            resultSet.close();
            preparedStatement.close();
            return uuids;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsAccount(@NotNull UUID uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM " + sql.getAccountTableName() + " WHERE " + AccountFields.UUID + "=?");
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
    public void updateFlair(@NotNull UUID uuid, int value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + sql.getAccountTableName() + " SET " + AccountFields.FLAIR + "=? WHERE " + AccountFields.UUID + "=?");
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAccount(@NotNull UUID uuid, @NotNull String redditUsername, boolean authenticated, int flair) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + sql.getAccountTableName() + " VALUES (?,?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, redditUsername);
            preparedStatement.setBoolean(3, authenticated);
            preparedStatement.setInt(4, flair);
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateRedditUsername(@NotNull UUID uuid, @NotNull String newRedditUsername) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + sql.getAccountTableName() + " SET " + AccountFields.REDDIT_USERNAME + "=? WHERE " + AccountFields.UUID + "=?");
            preparedStatement.setString(1, newRedditUsername);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateAuthenticated(@NotNull UUID uuid, boolean newAuthenticated) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + sql.getAccountTableName() + " SET " + AccountFields.AUTHENTICATED + "=? WHERE " + AccountFields.UUID + "=?");
            preparedStatement.setBoolean(1, newAuthenticated);
            preparedStatement.setString(2, uuid.toString());
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
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE " + sql.getAccountTableName() + " (" + AccountFields.UUID + " CHAR(36) NOT NULL, " + AccountFields.REDDIT_USERNAME + " VARCHAR(20) NOT NULL, " + AccountFields.AUTHENTICATED + " BOOLEAN NOT NULL, " + AccountFields.FLAIR + " TINYINT NOT NULL, PRIMARY KEY (" + AccountFields.UUID + "))");
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
            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE " + sql.getAccountTableName());
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
            ResultSet table = databaseMetaData.getTables(null, null, sql.getAccountTableName(), null);
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
