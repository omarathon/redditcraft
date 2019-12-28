package dev.omarathon.redditcraft.commands.flair.handlers;

import dev.omarathon.redditcraft.commands.flair.FlairSelector;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import org.bukkit.entity.Player;

public class UpdateHandler extends FlairPlayerHandler {
    private FlairManager flairManager;

    public UpdateHandler(FlairSelector from) {
        super("update", from);
        addArg("show");
        flairManager = from.getFlairManager();
    }

    @Override
    public void handleAuthenticatedPlayer(Player player, String[] args) {
        if (args.length != 0) {
            warnIncorrectUsage(player);
            return;
        }
        try {
            FlairStatus flairStatus = flairManager.update(player);
            Messaging.sendPrefixedMessage(player, Config.fillPlaceholder(commandMessages.getString("success"), "status", flairStatus.getFriendlyName()));
        }
        catch (Exception e) {
            Error.handleException(player, e, false);
            Messaging.sendPrefixedMessage(player, commandMessages.getString("error"));
        }
    }
}
