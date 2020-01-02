package dev.omarathon.redditcraft.subreddit.flair.manager;

import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.subreddit.SubredditManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairData;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import net.dean.jraw.models.Flair;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class FlairManager {
    // placeholders: <flair>, <username>
    private String displayFormat;
    protected ConfigurationSection flairsConfigSection;
    protected SubredditManager subredditManager;
    protected Map<String, Flair> flairMap; // key is the flair id, value is the flair
    private ConfigurationSection offConfigSection;
    protected EndpointEngine endpointEngine;
    protected Chat chat;
    protected Permission perm;

    private static final int FLAIR_MAX_LENGTH = 64;

    public FlairManager(SubredditManager subreddit) throws FlairException {
        this.subredditManager = subreddit;
        this.endpointEngine = subredditManager.getEndpointEngine();
        flairMap = new HashMap<>();
        for (Flair flair : subreddit.getSubredditReference().userFlairOptions()) {
            flairMap.put(flair.getId(), flair);
        }
        flairsConfigSection = subredditManager.getSubredditConfigSection().getConfigurationSection("flairs");
        displayFormat = flairsConfigSection.getString("format");
        offConfigSection = flairsConfigSection.getConfigurationSection("off");
        if (!flairMap.containsKey(offConfigSection.getString("id"))) {
            throw new FlairException(FlairException.Kind.FLAIR_NOT_EXIST);
        }
    }

    public void on(@NotNull OfflinePlayer offlinePlayer) throws FlairException {
        String playerName = offlinePlayer.getName();
        if (playerName == null) {
            throw new FlairException(FlairException.Kind.PLAYER_NOT_EXIST);
        }
        // fill username placeholder
        String usernameFilled = Config.fillPlaceholder(displayFormat, "username", playerName);
        // fill flair placeholder with nothing to compute remaining chars
        String usernameFlairFilled = Config.fillPlaceholder(usernameFilled, "flair", "");
        int charLimit = FLAIR_MAX_LENGTH - usernameFlairFilled.length();

        if (charLimit < 0) {
            throw new FlairException(FlairException.Kind.LENGTH_EXCEEDED);
        }

        FlairData flair = getFlair(offlinePlayer, charLimit);

        if (flair.getText().length() > charLimit) {
            throw new FlairException(FlairException.Kind.LENGTH_EXCEEDED);
        }

        flair.setText(Config.fillPlaceholder(usernameFilled, "flair", flair.getText()));

        flair.applyToMinecraftUser(offlinePlayer.getUniqueId(), subredditManager.getSubredditReference(), endpointEngine);
    }

    public void off(@NotNull OfflinePlayer offlinePlayer) throws FlairException {
        String playerName = offlinePlayer.getName();
        if (playerName == null) {
            throw new FlairException(FlairException.Kind.PLAYER_NOT_EXIST);
        }
        String flairMessage = Config.fillPlaceholder(offConfigSection.getString("format"), "username", playerName);

        if (flairMessage.length() > FLAIR_MAX_LENGTH) {
            throw new FlairException(FlairException.Kind.LENGTH_EXCEEDED);
        }

        FlairData flair = new FlairData(offConfigSection.getString("id"), flairMessage);
        flair.applyToMinecraftUser(offlinePlayer.getUniqueId(), subredditManager.getSubredditReference(), endpointEngine);
    }

    protected abstract FlairData getFlair(OfflinePlayer offlinePlayer, int charLimit) throws FlairException;

    public void remove(@NotNull OfflinePlayer offlinePlayer) throws FlairException {
        String redditUsername = endpointEngine.getRedditUsername(offlinePlayer.getUniqueId());
        if (redditUsername == null) {
            throw new FlairException(FlairException.Kind.NO_FOUND_REDDIT_USERNAME);
        }
        subredditManager.getSubredditReference().otherUserFlair(redditUsername).remove();
        endpointEngine.updateFlair(offlinePlayer.getUniqueId(), FlairStatus.NULL);
    }

    // obtains input offlinePlayer FlairStatus. if ON, calls ``on``. if OFF, calls ``off``. otherwise does nothing. returns their FlairStatus.
    public FlairStatus update(@NotNull OfflinePlayer offlinePlayer) throws FlairException {
        FlairStatus flairStatus = endpointEngine.getFlairStatus(offlinePlayer.getUniqueId());
        switch (flairStatus) {
            case ON:
                on(offlinePlayer);
                break;
            case OFF:
                off(offlinePlayer);
                break;
        }
        return flairStatus;
    }
}
