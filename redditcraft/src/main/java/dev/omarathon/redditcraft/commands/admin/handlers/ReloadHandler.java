package dev.omarathon.redditcraft.commands.admin.handlers;

import dev.omarathon.redditcraft.commands.admin.AdminSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class ReloadHandler extends Handler {
    public ReloadHandler(AdminSelector from) {
        super("reload", from);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length != 0) {
            warnIncorrectUsage(sender);
            return;
        }
        Messaging.sendPrefixedMessage(sender, commandMessages.getString("begin-prompt"));
        Reloader.reload(sender);
        Messaging.sendPrefixedMessage(sender, commandMessages.getString("success-prompt"));
    }
}
