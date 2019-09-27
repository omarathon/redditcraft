package dev.omarathon.redditcraft.commands.flair.handlers;

import dev.omarathon.redditcraft.commands.flair.FlairSelector;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.entity.Player;

public class OffHandler extends FlairPlayerHandler {
    public OffHandler(FlairSelector from) {
        super("off", from);
        addArg("false");
    }

    @Override
    public void handleAuthenticatedPlayer(Player player, String[] args) {
        if (args.length != 0) {
            warnIncorrectUsage(player);
            return;
        }
        try {
            endpointEngine.updateFlair(player.getUniqueId(), FlairStatus.OFF);
            Messaging.sendPrefixedMessage(player, commandMessages.getString("success"));
        }
        catch (Exception e) {
            Error.handleException(player, e, false);
            Messaging.sendPrefixedMessage(player, commandMessages.getString("error"));
        }
    }
}
