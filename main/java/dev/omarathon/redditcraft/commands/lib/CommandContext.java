package dev.omarathon.redditcraft.commands.lib;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// the context when someone executes a command
public class CommandContext {
    private CommandSender sender;
    private String[] args;

    public CommandContext(@NotNull CommandSender sender, @NotNull String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String[] getArgs() {
        return args;
    }
}
