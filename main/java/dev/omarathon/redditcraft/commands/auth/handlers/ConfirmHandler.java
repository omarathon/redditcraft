package dev.omarathon.redditcraft.commands.auth.handlers;

import dev.omarathon.redditcraft.commands.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.presets.PlayerOnlyHandler;
import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ConfirmHandler extends PlayerOnlyHandler {
    private FlairManager flairManager;
    private ConfirmQueue confirmQueue;

    public ConfirmHandler(AuthSelector from, ConfirmQueue confirmQueue) {
        super("confirm", from);
        this.confirmQueue = confirmQueue;
        this.flairManager = from.getFlairManager();
    }


    @Override
    public void handle(Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        if (args.length != 0) {
            Messaging.sendPrefixedMessage(player, commandMessages.getString("invalid-args-amount"));
            return;
        }
        String redditUsername = confirmQueue.getUuidRedditUsernameWindow().get(uuid);
        if (redditUsername == null) {
            Messaging.sendPrefixedMessage(player, commandMessages.getString("nothing-to-confirm"));
            return;
        }
        try {
            AuthStatus authStatus = endpointEngine.getAuthStatus(uuid);
            processConfirm(player, redditUsername, authStatus);
            confirmQueue.removeFromWindow(uuid);
        }
        catch (Exception e) {
            Error.handleException(player, e);
        }
    }

    private void processConfirm(Player player, String redditUsername, AuthStatus authStatus) {
        UUID uuid = player.getUniqueId();
        switch (authStatus) {
            case IN_PROGRESS:
                endpointEngine.updateExpiryTime(uuid);
                endpointEngine.updateRedditUsername(uuid, redditUsername);
                Messaging.sendPrefixedMessage(player, commandMessages.getString("success"));
                break;

            case AUTHENTICATED:
                flairManager.remove(player); // remove their flair
                endpointEngine.updateRedditUsername(uuid, redditUsername);
                endpointEngine.updateAuthenticatedStatus(uuid, false);
                if (endpointEngine.existsAuthRecord(uuid)) {
                    endpointEngine.updateExpiryTime(uuid);
                }
                else {
                    endpointEngine.addNewAuth(uuid);
                }
                Messaging.sendPrefixedMessage(player, commandMessages.getString("success"));
                break;

            default:
                Messaging.sendPrefixedMessage(player, commandMessages.getString("status-changed"));
                return;
        }
        Messaging.getLogger().info("Successfully began authentication for UUID " + uuid.toString() + " with reddit username " + redditUsername + " - waiting to verify (from confirm).");
    }
}
