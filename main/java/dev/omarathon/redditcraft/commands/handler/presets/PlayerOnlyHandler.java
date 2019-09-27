package dev.omarathon.redditcraft.commands.handler.presets;

import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerOnlyHandler extends Handler {
    private String playerOnlyMessage;

    public PlayerOnlyHandler(String formalArg, Selector from) {
        super(formalArg, from);
        playerOnlyMessage = Config.getSection("messages.general").getString("player-only-command");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Messaging.sendPrefixedMessage(sender, playerOnlyMessage);
            return;
        }
        handle((Player) sender, args);
    }

    public abstract void handle(Player player, String[] args);
}
