package dev.omarathon.redditcraft.commands.admin.flair.handlers;

import dev.omarathon.redditcraft.commands.admin.flair.FlairSelector;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UpdateHandler extends FlairAdminHandler {
    public UpdateHandler(FlairSelector from) {
        super("update", from, true, "&6Updating &eflair...", "&aSuccessfully updated flair.");
    }

    @Override
    public void handleSingle(CommandSender sender, UUID uuid, boolean quiet) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer.getName();
            if (name == null) {
                name = "&c&lUSERNAME NOT RESOLVABLE";
            }
            FlairStatus flairStatus = flairManager.update(Bukkit.getOfflinePlayer(uuid));
            if (!quiet) Messaging.sendPrefixedMessage(sender, "&eSuccessfully &6updated &eflair to " + flairStatus.getFriendlyName() + " &efor player &6" + name + " &e(UUID: &6" + uuid.toString() + "&e)");
            Messaging.getLogger().info("(ADMIN) Updated flair for UUID " + uuid.toString());
        }
        catch (Exception e) {
            Error.handleException(sender, e, false);
            if (!quiet) Messaging.sendPrefixedMessage(sender, "&cError updating flair for player with UUID &6" + uuid.toString());
        }
    }
}
