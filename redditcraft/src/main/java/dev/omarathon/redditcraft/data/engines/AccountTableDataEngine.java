package dev.omarathon.redditcraft.data.engines;

import dev.omarathon.redditcraft.data.endpoints.RedditUsernameAuthenticatedPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface AccountTableDataEngine extends TableDataEngine {
    @Nullable
    Boolean isAuthenticated(@NotNull UUID uuid);

    @Nullable
    String getRedditUsername(@NotNull UUID uuid);

    @Nullable
    RedditUsernameAuthenticatedPair getRedditUsernameAuthenticatedPair(@NotNull UUID uuid);

    @Nullable
    Integer getFlair(@NotNull UUID uuid);

    @NotNull
    Iterable<UUID> getAllUuidsWithRedditUsername(@NotNull String redditUsername);

    @NotNull
    Iterable<UUID> getAllAuthenticatedUuidsWithRedditUsername(@NotNull String redditUsername);

    @NotNull
    Iterable<UUID> getAllAuthenticatedUuids();

    boolean existsAccount(@NotNull UUID uuid);

    void updateFlair(@NotNull UUID uuid, int value);
    void addAccount(@NotNull UUID uuid, @NotNull String redditUsername, boolean authenticated, int flair);
    void updateRedditUsername(@NotNull UUID uuid, @NotNull String newRedditUsername);
    void updateAuthenticated(@NotNull UUID uuid, boolean newAuthenticated);
}
