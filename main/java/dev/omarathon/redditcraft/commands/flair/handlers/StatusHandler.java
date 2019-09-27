package dev.omarathon.redditcraft.commands.flair.handlers;

import dev.omarathon.redditcraft.commands.flair.FlairSelector;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.entity.Player;

public class StatusHandler extends FlairPlayerHandler {
    public StatusHandler(FlairSelector from) {
        super("status", from);
    }

    @Override
    public void handleAuthenticatedPlayer(Player player, String[] args) {
        if (args.length != 0) {
            warnIncorrectUsage(player);
            return;
        }
        try {
            FlairStatus flairStatus = endpointEngine.getFlairStatus(player.getUniqueId());
            Messaging.sendPrefixedMessage(player, flairStatus.getMessage(false));
        }
        catch (Exception e) {
            Error.handleException(player, e, true);
        }
    }
}
