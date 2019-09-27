package dev.omarathon.redditcraft.commands.flair.handlers;

import dev.omarathon.redditcraft.commands.flair.FlairSelector;
import dev.omarathon.redditcraft.commands.handler.presets.PlayerOnlyHandler;
import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.entity.Player;

public abstract class FlairPlayerHandler extends PlayerOnlyHandler {
    private static String notAuthenticatedMessage = Config.getSection("messages.flair").getString("not-authenticated");

    public FlairPlayerHandler(String formalArg, FlairSelector from) {
        super(formalArg, from);
    }

    @Override
    public void handle(Player player, String[] args) {
        AuthStatus authStatus;
        try {
            authStatus = endpointEngine.getAuthStatus(player.getUniqueId());
        }
        catch (Exception e) {
            Error.handleException(player, e);
            return;
        }
        if (authStatus == AuthStatus.AUTHENTICATED) {
            handleAuthenticatedPlayer(player, args);
        }
        else {
            Messaging.sendPrefixedMessage(player, notAuthenticatedMessage);
        }
    }

    public abstract void handleAuthenticatedPlayer(Player player, String[] args);
}
