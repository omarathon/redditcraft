package dev.omarathon.redditcraft.commands.handler.presets;

import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class HelpHandler extends Handler {
    private String helpText;

    public HelpHandler(Selector fromSelector) {
        super("help", fromSelector);
        addArg("?");
        this.helpText = commandMessages.getString("message");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Messaging.sendPrefixedMessage(sender, helpText);
    }
}
