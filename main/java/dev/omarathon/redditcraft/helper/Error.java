package dev.omarathon.redditcraft.helper;

import org.bukkit.command.CommandSender;

public class Error {
    // by default we tell the user an error occured
    public static void handleException(CommandSender sender, Exception e) {
        handleException(sender, e, true);
    }

    public static void handleException(CommandSender sender, Exception e, boolean notify) {
        if (notify) Messaging.notifyError(sender);
        Messaging.getLogger().severe(e.getMessage());
        e.printStackTrace();
    }
}
