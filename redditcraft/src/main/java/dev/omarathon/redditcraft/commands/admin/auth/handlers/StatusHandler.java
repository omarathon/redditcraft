package dev.omarathon.redditcraft.commands.admin.auth.handlers;

import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class StatusHandler extends Handler {
    public StatusHandler(AuthSelector from) {
        super("status", from);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length != 1) {
            warnIncorrectUsage(sender);
            return;
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(args[0]);
        }
        catch (IllegalArgumentException e) {
            Messaging.sendPrefixedMessage(sender, "&cProvided UUID is invalid!");
            return;
        }
        String username = Bukkit.getOfflinePlayer(uuid).getName();
        if (username == null) {
            Messaging.sendPrefixedMessage(sender, "&e&lWARNING: &ePlayer name not resolvable!");
        }
        else {
            Messaging.sendPrefixedMessage(sender, "&e&lUSERNAME: &e" + username);
        }
        AuthStatus authStatus = null;
        try {
            authStatus = endpointEngine.getAuthStatus(uuid);
            Messaging.sendPrefixedMessage(sender, authStatus.getMessage(true, uuid, endpointEngine));
        }
        catch (Exception e) {
            Error.handleException(sender, e);
            return;
        }

    }
}
