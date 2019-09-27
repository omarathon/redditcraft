package dev.omarathon.redditcraft.commands;

import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.selector.SelectorArgs;
import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// command executor for /rc or /redditcraft
public class MainCommandExecutor implements CommandExecutor {
    private MainSelector mainSelector;

    public MainCommandExecutor(AuthManager authManager, FlairManager flairManager, EndpointEngine endpointEngine) {
        this.mainSelector = new MainSelector(authManager, flairManager, endpointEngine);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            args = new String[]{"help"};
        }
        mainSelector.execute(new SelectorArgs(args, sender));
        return true;
    }
}
