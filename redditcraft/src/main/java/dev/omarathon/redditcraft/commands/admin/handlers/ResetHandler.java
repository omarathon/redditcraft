package dev.omarathon.redditcraft.commands.admin.handlers;

import dev.omarathon.redditcraft.commands.admin.AdminSelector;
import dev.omarathon.redditcraft.commands.admin.flair.handlers.RemoveHandler;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class ResetHandler extends Handler {
    private RemoveHandler removeHandler;

    public ResetHandler(AdminSelector from) {
        super("reset", from);
        this.removeHandler = from.getFlairSelector().getRemoveHandler();
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length != 0) {
            warnIncorrectUsage(sender);
            return;
        }
        Messaging.sendPrefixedMessage(sender, "&eAttempting to &c&lremove &eall flairs...");
        removeHandler.handle(sender, new String[]{"*"});

        Messaging.sendPrefixedMessage(sender, "&cAttempting to delete MySQL tables...");
        try {
            endpointEngine.deleteAuthTable();
            Messaging.sendPrefixedMessage(sender, "&eSuccessfully deleted auth table!");
            endpointEngine.deleteAccountTable();
            Messaging.sendPrefixedMessage(sender, "&eSuccessfully deleted accounts table!");
        }
        catch (Exception e) {
            Error.handleException(sender, e);
        }
        Messaging.sendPrefixedMessage(sender, "&cReloading...");
        Reloader.reload(sender);
        Messaging.sendPrefixedMessage(sender, "&aSuccessfully reset database!");

    }
}
