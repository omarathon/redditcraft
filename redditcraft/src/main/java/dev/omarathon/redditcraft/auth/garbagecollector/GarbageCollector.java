package dev.omarathon.redditcraft.auth.garbagecollector;

import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class GarbageCollector {
    private EndpointEngine endpointEngine;

    public GarbageCollector(EndpointEngine endpointEngine) {
        this.endpointEngine = endpointEngine;
    }

    public void run(CommandSender sender) {
        try {
            Messaging.getLogger().info("&eRunning Garbage Collector...");
            endpointEngine.removeExpiredAuthRecords();
            Messaging.getLogger().info("&aSuccessfully ran Garbage Collector!");
        }
        catch (Exception e) {
            Error.handleException(sender, e);
        }
    }
}
