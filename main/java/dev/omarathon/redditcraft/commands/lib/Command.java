package dev.omarathon.redditcraft.commands.lib;

import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

// a command is either a selector or a handler
public class Command {
    private String formalArg;
    private Set<String> args;
    private String noPermission;
    private String incorrectUsage;
    private Command parent;
    private LinkedList<String> permission;

    public Command(String formalArg, Command parent) {
        ConfigurationSection messages = Config.getSection("messages.general");
        this.incorrectUsage = messages.getString("incorrect-usage");
        this.noPermission = messages.getString("no-permission");
        this.formalArg = formalArg;
        if (formalArg != null) {
            args = new HashSet<>(1);
            args.add(formalArg);
        }
        this.parent = parent;
        loadPermission();
    }

    private void loadPermission() {
        if (parent == null) {
            permission = new LinkedList<>();
        }
        else {
            permission = (LinkedList<String>) parent.getPermission().clone();
        }
        permission.addLast(formalArg);
    }

    public void addArg(String arg) {
        args.add(arg);
    }

    public void addArgs(Iterable<String> args) {
        for (String arg : args) {
            this.args.add(arg);
        }
    }

    public Set<String> getArgs() {
        return args;
    }

    public void setIncorrectUsageMessage(String incorrectUsage) {
        this.incorrectUsage = incorrectUsage;
    }

    public void setNoPermissionMessage(String noPermission) {
        this.noPermission = noPermission;
    }

    public PermissionResult hasPermission(CommandSender sender) {
        String last = permission.getLast();
        if (last == null) {
            return PermissionResult.EMPTY;
        }
        permission.removeLast();
        permission.add("*");
        String wildcardPermission = String.join(".", permission);
        permission.removeLast();
        permission.add(last);
        String permissionString = String.join(".", permission);

        if (sender.hasPermission(wildcardPermission)) {
            return PermissionResult.WILDCARDED;
        }

        if (sender.hasPermission(permissionString)) {
            return PermissionResult.ALLOWED;
        }
        return PermissionResult.REJECTED;
    }

    public void warnIncorrectUsage(CommandSender sender) {
        Messaging.sendPrefixedMessage(sender, incorrectUsage);
    }

    public void warnNoPermission(CommandSender sender) {
        Messaging.sendPrefixedMessage(sender, noPermission);
    }

    public String getFormalArg() {
        return formalArg;
    }

    public Command getParent() {
        return parent;
    }

    public LinkedList<String> getPermission() {
        return permission;
    }
}
