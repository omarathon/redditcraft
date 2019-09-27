package dev.omarathon.redditcraft.data;

import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.data.endpoints.RedditUsernameAuthenticatedPair;
import dev.omarathon.redditcraft.data.engines.AccountTableDataEngine;
import dev.omarathon.redditcraft.data.engines.AuthTableDataEngine;
import dev.omarathon.redditcraft.helper.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EndpointEngine {
    private AccountTableDataEngine accountTableDataEngine;
    private AuthTableDataEngine authTableDataEngine;

    private long authWindowMins;

    public EndpointEngine(@NotNull AccountTableDataEngine accountTableDataEngine, @NotNull AuthTableDataEngine authTableDataEngine) {
        this.accountTableDataEngine = accountTableDataEngine;
        this.authTableDataEngine = authTableDataEngine;
    }

    private void initFields() {
        authWindowMins = Config.getSection("auth").getLong("window-mins");
    }

    @NotNull
    private LocalDateTime getNewAuthExpiryTime() {
        return LocalDateTime.now().plusMinutes(authWindowMins);
    }

    @NotNull
    public AuthStatus getAuthStatus(@NotNull UUID uuid) {
        LocalDateTime authExpiry = authTableDataEngine.getExpiryTime(uuid);
        boolean expiredAuth = false;
        if (authExpiry != null) {
            if (authExpiry.isAfter(LocalDateTime.now())) {
                return AuthStatus.IN_PROGRESS;
            }
            else {
                expiredAuth = true;
            }
        }
        Boolean accountAuthenticated = accountTableDataEngine.isAuthenticated(uuid);
        if (accountAuthenticated == null) {
            return AuthStatus.NOT_EXISTING;
        }
        else {
            if (accountAuthenticated) {
                return AuthStatus.AUTHENTICATED;
            }
            else {
                if (expiredAuth) {
                    return AuthStatus.NEW_FAILED;
                }
                else {
                    return AuthStatus.NEW;
                }
            }
        }
    }

    public boolean accountTableExists() {
        return accountTableDataEngine.exists();
    }

    public boolean authTableExists() {
        return authTableDataEngine.exists();
    }

    public void createAccountTable() {
        accountTableDataEngine.create();
    }

    public void createAuthTable() {
        authTableDataEngine.create();
    }

    public void deleteAccountTable() {
        accountTableDataEngine.delete();
    }

    public void deleteAuthTable() {
        authTableDataEngine.delete();
    }

    @Nullable
    public FlairStatus getFlairStatus(@NotNull UUID uuid) throws IllegalStateException {
        Integer flair = accountTableDataEngine.getFlair(uuid);
        if (flair == null) {
            return null;
        }
        return FlairStatus.fromInternalInt(flair);
    }

    public void updateFlair(@NotNull UUID uuid, @NotNull FlairStatus flairStatus) {
        accountTableDataEngine.updateFlair(uuid, flairStatus.getInternalInt());
    }

    @Nullable
    public String getRedditUsername(@NotNull UUID uuid) {
        return accountTableDataEngine.getRedditUsername(uuid);
    }

    public void addNewAuth(@NotNull UUID uuid) {
        authTableDataEngine.addToken(uuid, getNewAuthExpiryTime());
    }

    public void addNewAccount(@NotNull UUID uuid, @NotNull String redditUsername, boolean authenticated) {
        accountTableDataEngine.addAccount(uuid, redditUsername, authenticated, 0);
    }

    public void updateRedditUsername(@NotNull UUID uuid, @NotNull String newRedditUsername) {
        accountTableDataEngine.updateRedditUsername(uuid, newRedditUsername);
    }

    public void updateAuthenticatedStatus(@NotNull UUID uuid, boolean newAuthenticated) {
        accountTableDataEngine.updateAuthenticated(uuid, newAuthenticated);
    }

    public void updateExpiryTime(@NotNull UUID uuid) {
        authTableDataEngine.updateExpiryTime(uuid, getNewAuthExpiryTime());
    }

    public boolean existsAuthRecord(@NotNull UUID uuid) {
        return authTableDataEngine.existsToken(uuid);
    }

    public void removeExpiredAuthRecords() {
        authTableDataEngine.removeAllExpiredTokens();
    }

    // attempts to verify the uuid with the input redditUsername (so change authenticated to true in accounts if they're verified). returns true is successful, false if not
    public boolean verifyPlayerRedditAccount(@NotNull UUID uuid, @NotNull String redditUsername) {
        if (!accountTableDataEngine.getRedditUsername(uuid).equals(redditUsername)) {
            return false;
        }
        LocalDateTime authExpiry = authTableDataEngine.getExpiryTime(uuid);
        if (authExpiry != null) {
            if (LocalDateTime.now().isBefore(authExpiry)) {
                updateAuthenticatedStatus(uuid, true);
                authTableDataEngine.removeToken(uuid);
                return true;
            }
            authTableDataEngine.removeToken(uuid);
        }
        return false;
    }


    @Nullable
    public LocalDateTime getAuthExpiry(@NotNull UUID uuid) {
        return authTableDataEngine.getExpiryTime(uuid);
    }

    public boolean existsAccountRecord(@NotNull UUID uuid) {
        return accountTableDataEngine.existsAccount(uuid);
    }

    public void removeAuthRecord(@NotNull UUID uuid) {
        authTableDataEngine.removeToken(uuid);
    }

    public void removeAllAuthRecords() {
        authTableDataEngine.removeAllTokens();
    }

    @NotNull
    public List<UUID> getAuthenticatedUuidsWithRedditUsername(@NotNull String redditUsername) {
        List<UUID> uuids = new ArrayList<>(1);
        accountTableDataEngine.getAllAuthenticatedUuidsWithRedditUsername(redditUsername).forEach(uuids::add);
        return uuids;
    }

    @NotNull
    public List<UUID> getUuidsWithRedditUsername(@NotNull String redditUsername) {
        List<UUID> uuids = new ArrayList<>(1);
        accountTableDataEngine.getAllUuidsWithRedditUsername(redditUsername).forEach(uuids::add);
        return uuids;
    }

    @NotNull
    public List<UUID> getAuthenticatedUuids() {
        List<UUID> uuids = new ArrayList<>();
        accountTableDataEngine.getAllAuthenticatedUuids().forEach(uuids::add);
        return uuids;
    }

    @Nullable
    public RedditUsernameAuthenticatedPair getRedditUsernameAuthenticatedPair(@NotNull UUID uuid) {
        return accountTableDataEngine.getRedditUsernameAuthenticatedPair(uuid);
    }

}
