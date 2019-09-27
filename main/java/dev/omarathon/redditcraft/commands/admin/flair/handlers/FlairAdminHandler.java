package dev.omarathon.redditcraft.commands.admin.flair.handlers;

import dev.omarathon.redditcraft.commands.admin.flair.FlairSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public abstract class FlairAdminHandler extends Handler {
    private boolean allFunctionality;
    protected FlairManager flairManager;
    private String beginMessage;
    private String endMessage;

    public FlairAdminHandler(String formalArg, FlairSelector from, boolean allFunctionality, String beginMessage, String endMessage) {
        super(formalArg, from);
        flairManager = from.getFlairManager();
        this.allFunctionality = allFunctionality;
        this.beginMessage = beginMessage;
        this.endMessage = endMessage;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if ((args.length > 2) || (args.length == 0)) {
            warnIncorrectUsage(sender);
            return;
        }
        boolean quiet = (args.length == 2) && (args[1].equalsIgnoreCase("quiet") || args[1].equalsIgnoreCase("silent"));
        String arg = args[0];
        if (arg.equals("all") || arg.equals("*")) {
            if (allFunctionality) {
                List<UUID> allAuthenticatedUuids;
                try {
                    allAuthenticatedUuids = endpointEngine.getAuthenticatedUuids();
                }
                catch (Exception e) {
                    Error.handleException(sender, e);
                    return;
                }
                Messaging.sendPrefixedMessage(sender, beginMessage + " &c&l(ALL)");
                handleAll(sender, allAuthenticatedUuids, quiet);
                Messaging.sendPrefixedMessage(sender, endMessage + " &c&l(ALL)");
            }
            else {
                Messaging.sendPrefixedMessage(sender, "&cAll not supported by this command!");
            }
            return;
        }
        // is uuid
        UUID uuid;
        try {
            uuid = UUID.fromString(arg);
        }
        catch (IllegalArgumentException e) {
            Messaging.sendPrefixedMessage(sender, "&cUUID is of an invalid format!");
            return;
        }
        Messaging.sendPrefixedMessage(sender, beginMessage);
        handleSingle(sender, uuid, quiet);
        Messaging.sendPrefixedMessage(sender, endMessage);
    }

    public void handleAll(CommandSender sender, List<UUID> uuids, boolean quiet) {
        for (UUID uuid : uuids) {
            handleSingle(sender, uuid, quiet);
        }
    }

    public abstract void handleSingle(CommandSender sender, UUID uuid, boolean quiet);
}
