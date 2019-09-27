package dev.omarathon.redditcraft.commands.admin.auth.garbage;

import dev.omarathon.redditcraft.auth.garbagecollector.GarbageCollector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class RunHandler extends Handler {
    private GarbageCollector garbageCollector;

    public RunHandler(GarbageSelector from) {
        super("run", from);
        garbageCollector = from.getGarbageCollector();
        addArg("collect");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Messaging.sendPrefixedMessage(sender, "Running garbage collector...");
        garbageCollector.run(sender);
    }
}
