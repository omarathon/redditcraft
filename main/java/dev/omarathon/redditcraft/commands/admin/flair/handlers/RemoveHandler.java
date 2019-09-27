package dev.omarathon.redditcraft.commands.admin.flair.handlers;

import dev.omarathon.redditcraft.commands.admin.flair.FlairSelector;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class RemoveHandler extends FlairAdminHandler {
    public RemoveHandler(FlairSelector from) {
        super("remove", from, true, "&cRemoving &eflair...", "&aSuccessfully removed flair.");
    }

    @Override
    public void handleSingle(CommandSender sender, UUID uuid, boolean quiet) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer.getName();
            if (name == null) {
                name = "&c&lUSERNAME NOT RESOLVABLE";
            }
            flairManager.remove(Bukkit.getOfflinePlayer(uuid));
            if (!quiet) Messaging.sendPrefixedMessage(sender, "&eSuccessfully &cremoved &eflair for player &6" + name + " &e(UUID: &6" + uuid.toString() + "&e)");
            Messaging.getLogger().info("(ADMIN) Removed flair for UUID " + uuid.toString());
        }
        catch (Exception e) {
            Error.handleException(sender, e, false);
            if (!quiet) Messaging.sendPrefixedMessage(sender, "&cError turning on flair for player with UUID &6" + uuid.toString());
        }
    }
}
