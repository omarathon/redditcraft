package dev.omarathon.redditcraft.commands.admin.flair.handlers;

import dev.omarathon.redditcraft.commands.admin.flair.FlairSelector;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class OffHandler extends FlairAdminHandler {
    public OffHandler(FlairSelector from) {
        super("off", from, true, "&eTurning &coff &eflair...", "&aSuccessfully turned off flair.");
    }

    @Override
    public void handleSingle(CommandSender sender, UUID uuid, boolean quiet) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer.getName();
            if (name == null) {
                name = "&c&lUSERNAME NOT RESOLVABLE";
            }
            flairManager.off(Bukkit.getOfflinePlayer(uuid));
            if (!quiet) Messaging.sendPrefixedMessage(sender, "&eSuccessfully turned &coff &eflair for player &6" + name + " &e(UUID: &6" + uuid.toString() + "&e)");
            Messaging.getLogger().info("(ADMIN) Turned off flair for UUID " + uuid.toString());
        }
        catch (Exception e) {
            Error.handleException(sender, e, false);
            if (!quiet) Messaging.sendPrefixedMessage(sender, "&cError turning off flair for player with UUID &6" + uuid.toString());
        }
    }
}
