package dev.omarathon.redditcraft.commands.auth.handlers;

import dev.omarathon.redditcraft.commands.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.presets.PlayerOnlyHandler;
import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.helper.RedditHelper;
import dev.omarathon.redditcraft.reddit.Reddit;
import net.dean.jraw.models.AccountQuery;
import net.dean.jraw.models.AccountStatus;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BeginHandler extends PlayerOnlyHandler {
    private ConfirmQueue confirmQueue;

    public BeginHandler(AuthSelector from, ConfirmQueue confirmQueue) {
        super("begin", from);
        this.confirmQueue = confirmQueue;
    }

    @Override
    public void handle(Player player, String[] args) {
        String redditUsername = obtainRedditUsername(player, args);
        if (redditUsername == null) {
            return;
        }
        try {
            UUID uuid = player.getUniqueId();
            AuthStatus authStatus = endpointEngine.getAuthStatus(uuid);

            Messaging.sendPrefixedMessage(player, authStatus.getMessage(false, uuid, endpointEngine));
            Messaging.sendPrefixedMessage(player, getOnBeginMessage(player.getName(), redditUsername, authStatus));

            processAuthStatus(player, authStatus, redditUsername);
        }
        catch (Exception e) {
            Error.handleException(player, e);
            return;
        }
    }

    private String getOnBeginMessage(@NotNull String minecraftUsername, @NotNull String redditUsername, AuthStatus authStatus) {
        ConfigurationSection authSection = Config.getSection("auth");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("bot-name", Reddit.getBotUsername());
        placeholders.put("subject", authSection.getConfigurationSection("verification").getString("reddit-message-subject"));
        placeholders.put("username", redditUsername);
        placeholders.put("window", Long.toString(authSection.getConfigurationSection("verification").getLong("cooldown-seconds")));
        placeholders.put("minecraft-username", minecraftUsername);
        String onBeginMessage = Config.fillPlaceholders(commandMessages.getString("prompt").replace("%n", "\n \n"), placeholders);

        if (authStatus == AuthStatus.IN_PROGRESS || authStatus == AuthStatus.AUTHENTICATED) {
            placeholders.clear();
            placeholders.put("confirm-window", Long.toString(authSection.getLong("confirmation-window-seconds")));
            String confirmMessage = Config.fillPlaceholders(commandMessages.getString("confirm-prompt"), placeholders);
            return onBeginMessage + "\n \n" + confirmMessage;
        }
        return onBeginMessage;
    }

    private void processAuthStatus(Player player, AuthStatus authStatus, String redditUsername) {
        UUID uuid = player.getUniqueId();
        switch (authStatus) {
            case NOT_EXISTING:
                endpointEngine.addNewAccount(uuid, redditUsername, false);
                endpointEngine.addNewAuth(uuid);
                break;

            case NEW:
                endpointEngine.updateRedditUsername(uuid, redditUsername);
                endpointEngine.addNewAuth(uuid);
                break;

            case NEW_FAILED:
                endpointEngine.updateRedditUsername(uuid, redditUsername);
                endpointEngine.updateExpiryTime(uuid);
                break;

            case IN_PROGRESS:
            case AUTHENTICATED:
                confirmQueue.addToWindow(uuid, redditUsername);
                return;
        }
        Messaging.getLogger().info("Successfully began authentication for UUID " + uuid.toString() + " with reddit username " + redditUsername + " - waiting to verify.");
    }

    private String obtainRedditUsername(Player player, String[] args) {
        if (!(args.length == 1)) {
            Messaging.sendPrefixedMessage(player, commandMessages.getString("invalid-args-amount"));
            return null;
        }
        String username = args[0];
        if (!RedditHelper.validUsername(username)) {
            Messaging.sendPrefixedMessage(player, commandMessages.getString("invalid-username-format"));
            return null;
        }
        AccountQuery accountQuery = Reddit.getRedditUser(username);
        if (accountQuery.getStatus() != AccountStatus.EXISTS) {
            Messaging.sendPrefixedMessage(player, commandMessages.getString("non-existing-username"));
            return null;
        }
        return accountQuery.getAccount().getName();
    }
}
