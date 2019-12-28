package dev.omarathon.redditcraft.commands.flair.handlers;

import dev.omarathon.redditcraft.commands.flair.FlairSelector;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import org.bukkit.entity.Player;

public class RemoveHandler extends FlairPlayerHandler {
    private FlairManager flairManager;

    public RemoveHandler(FlairSelector from) {
        super("remove", from);
        flairManager = from.getFlairManager();
        addArg("null");
    }

    @Override
    public void handleAuthenticatedPlayer(Player player, String[] args) {
        if (args.length != 0) {
            warnIncorrectUsage(player);
            return;
        }
        try {
            flairManager.remove(player);
            Messaging.sendPrefixedMessage(player, commandMessages.getString("success"));
        }
        catch (Exception e) {
            Error.handleException(player, e, false);
            Messaging.sendPrefixedMessage(player, commandMessages.getString("error"));
        }
    }
}
