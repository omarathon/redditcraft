package dev.omarathon.redditcraft.commands.selector;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectorArgs {
    private List<String> args;
    private int argIndex;
    private CommandSender sender;
    boolean wildcarded;



    public SelectorArgs(@NotNull String[] args, @NotNull CommandSender sender) throws IllegalArgumentException {
        if (args.length == 0) throw new IllegalArgumentException();
        this.args = new ArrayList<>(Arrays.asList(args));
        this.argIndex = 0;
        this.sender = sender;
        wildcarded = false;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getArg() {
        return args.get(argIndex);
    }

    public void setArg(String value) {
        args.set(argIndex, value);
    }

    public int getArgIndex() {
        return argIndex;
    }

    public CommandSender getSender() {
        return sender;
    }

    public void incrementArgIndex() throws IllegalStateException {
        if (argIndex == args.size() - 1) throw new IllegalStateException();
        argIndex++;
    }

    public boolean isWildcarded() {
        return wildcarded;
    }

    public void setWildcarded(boolean wildcarded) {
        this.wildcarded = wildcarded;
    }
}
