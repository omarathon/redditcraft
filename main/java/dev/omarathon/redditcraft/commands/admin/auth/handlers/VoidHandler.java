package dev.omarathon.redditcraft.commands.admin.auth.handlers;

import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class VoidHandler extends Handler {
    private FlairManager flairManager;

    public VoidHandler(AuthSelector from) {
        super("void", from);
        flairManager = from.getFlairManager();
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
        try {
            endpointEngine.removeAuthRecord(uuid);
            AuthStatus authStatus = endpointEngine.getAuthStatus(uuid);
            if (authStatus == AuthStatus.AUTHENTICATED) {
                Messaging.sendPrefixedMessage(sender, "&e&lWARNING: &ePlayer had an authenticated reddit account with username &6" + endpointEngine.getRedditUsername(uuid) + " &e- voiding!");
            }
            if (authStatus != AuthStatus.NOT_EXISTING) {
                endpointEngine.updateAuthenticatedStatus(uuid, false);
                flairManager.remove(Bukkit.getOfflinePlayer(uuid));
            }
        }
        catch (Exception e) {
            Error.handleException(sender, e);
            return;
        }
        Messaging.sendPrefixedMessage(sender, "&aSuccessfully voided authentication!");
    }
}
