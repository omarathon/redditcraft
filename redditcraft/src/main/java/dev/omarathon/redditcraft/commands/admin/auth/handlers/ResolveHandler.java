package dev.omarathon.redditcraft.commands.admin.auth.handlers;

import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.helper.RedditHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class ResolveHandler extends Handler {
    private AuthManager authManager;

    public ResolveHandler(AuthSelector from) {
        super("resolve", from);
        authManager = from.getAuthManager();
        addArg("whois");
        addArg("get");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length != 2 && args.length != 3) {
            warnIncorrectUsage(sender);
            return;
        }
        boolean all;
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("all") || args[2].equals("*")) {
                all = true;
            }
            else {
                warnIncorrectUsage(sender);
                return;
            }
        }
        else {
            all = false;
        }
        String kind = args[0];
        if (kind.equalsIgnoreCase("m") || kind.equalsIgnoreCase("mc") || kind.equalsIgnoreCase("minecraft")) {
            resolveMinecraft(sender, args[1], all);
        }
        else if (kind.equalsIgnoreCase("r") || kind.equalsIgnoreCase("reddit")) {
            resolveReddit(sender, args[1], all);
        }
        else {
            warnIncorrectUsage(sender);
        }
    }

    private void resolveReddit(CommandSender sender, String redditUsername, boolean all) {
        if (!RedditHelper.validUsername(redditUsername)) {
            Messaging.sendPrefixedMessage(sender, "&cGiven reddit username isn't valid!");
            return;
        }
        List<UUID> uuids;
        try {
            if (all) uuids = endpointEngine.getUuidsWithRedditUsername(redditUsername);
            else uuids = endpointEngine.getAuthenticatedUuidsWithRedditUsername(redditUsername);
        }
        catch (Exception e) {
            Error.handleException(sender, e);
            return;
        }
        String word = all ? "associated" : "authenticated";
        StringBuilder message = new StringBuilder("&eThe following minecraft accounts have been found " + word + " with the reddit username &6" + redditUsername + "&e:\n");
        for (UUID uuid : uuids) {
            String username = Bukkit.getOfflinePlayer(uuid).getName();
            if (username == null) {
                username = "&c&lUSERNAME NOT RESOLVABLE";
            }
            message.append(" - &6" + username + " &e(UUID: &6" + uuid.toString() + "&e)\n");
        }
        Messaging.sendPrefixedMessage(sender, message.toString());
    }

    private void resolveMinecraft(CommandSender sender, String uuidString, boolean all) {
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
        }
        catch (IllegalArgumentException e) {
            Messaging.sendPrefixedMessage(sender, "&cGiven uuid is not valid!");
            return;
        }
        String word = all ? "associated" : "authenticated";
        String redditUsername;
        try {
            redditUsername = endpointEngine.getRedditUsername(uuid);
        }
        catch (Exception e) {
            Error.handleException(sender, e);
            return;
        }
        boolean found = true;
        if (redditUsername == null) {
            found = false;
        }
        else {
            if (!all) {
                AuthStatus authStatus;
                try {
                    authStatus = endpointEngine.getAuthStatus(uuid);
                }
                catch (Exception e) {
                    Error.handleException(sender, e);
                    return;
                }
                found = authStatus == AuthStatus.AUTHENTICATED;
            }
        }
        if (found) {
            Messaging.sendPrefixedMessage(sender, "&aResolved " + word + " reddit account: &6" + redditUsername);
        }
        else {
            Messaging.sendPrefixedMessage(sender, "&cNo reddit accounts found " + word + " with the minecraft player of UUID &6" + uuid.toString());
        }
    }
}
