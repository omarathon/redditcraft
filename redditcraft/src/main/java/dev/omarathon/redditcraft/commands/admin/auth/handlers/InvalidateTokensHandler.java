package dev.omarathon.redditcraft.commands.admin.auth.handlers;

import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class InvalidateTokensHandler extends Handler {
    public InvalidateTokensHandler(AuthSelector from) {
        super("invalidatetokens", from);
        addArg("canceltokens");
        addArg("it");
        addArg("ct");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Messaging.sendPrefixedMessage(sender, "&cAttempting to invalidate all in-progress authentication tokens...");
        try {
            endpointEngine.removeAllAuthRecords();
        }
        catch (Exception e) {
            Error.handleException(sender, e);
            return;
        }
        Messaging.sendPrefixedMessage(sender, "&aSuccessfully invalidated all in-progress authentication tokens!");
    }
}
