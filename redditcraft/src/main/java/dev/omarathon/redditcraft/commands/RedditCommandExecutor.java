package dev.omarathon.redditcraft.commands;

import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RedditCommandExecutor implements CommandExecutor {
    private EndpointEngine endpointEngine;
    private String message;
    private String incorrectUsage;

    public RedditCommandExecutor(EndpointEngine endpointEngine) {
        this.endpointEngine = endpointEngine;
        String subreddit = "https://www.reddit.com/r/" + Config.getSection("subreddit").getString("name");
        message = Config.fillPlaceholder(Config.getSection("messages.commands.reddit").getString("message"), "subreddit", subreddit);
        incorrectUsage = Config.getSection("messages.general").getString("incorrect-usage");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            Messaging.sendPrefixedMessage(sender, incorrectUsage);
        }
        else {
            Messaging.sendPrefixedMessage(sender, message);
        }
        return true;
    }
}
