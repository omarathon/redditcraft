package dev.omarathon.redditcraft.commands.admin.handlers;

import dev.omarathon.redditcraft.RedditCraft;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class Reloader {
    public static void reload(CommandSender sender) {
        Messaging.getLogger().warning("Reloading RedditCraft...");
        RedditCraft.getInstance().reloadConfig();
        RedditCraft.getInstance().load();
        Messaging.getLogger().info("Reload successful!");
    }
}
