package dev.omarathon.redditcraft.commands.auth.handlers;

import dev.omarathon.redditcraft.commands.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.presets.PlayerOnlyHandler;
import dev.omarathon.redditcraft.data.endpoints.AuthStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.entity.Player;

public class StatusHandler extends PlayerOnlyHandler {
    public StatusHandler(AuthSelector from) {
        super("status", from);
    }

    @Override
    public void handle(Player sender, String[] args) {
        if (args.length != 0) {
            warnIncorrectUsage(sender);
            return;
        }
        try {
            AuthStatus authStatus = endpointEngine.getAuthStatus(sender.getUniqueId());
            Messaging.sendPrefixedMessage(sender, authStatus.getMessage(false, sender.getUniqueId(), endpointEngine));
        }
        catch (Exception e){
            Error.handleException(sender, e);
            return;
        }
    }
}
