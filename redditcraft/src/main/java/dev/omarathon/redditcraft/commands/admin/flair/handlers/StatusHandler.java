package dev.omarathon.redditcraft.commands.admin.flair.handlers;

import dev.omarathon.redditcraft.commands.admin.flair.FlairSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class StatusHandler extends Handler {
    public StatusHandler(FlairSelector from) {
        super("status", from);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length != 1) {
            warnIncorrectUsage(sender);
            return;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        }
        catch (IllegalArgumentException e) {
            Messaging.sendPrefixedMessage(sender, "&cGiven UUID is invalid!");
            return;
        }
        FlairStatus flairStatus;
        try {
            flairStatus = endpointEngine.getFlairStatus(uuid);
        }
        catch (Exception e) {
            Error.handleException(sender, e);
            return;
        }
        Messaging.sendPrefixedMessage(sender, flairStatus.getMessage(true));
    }
}
