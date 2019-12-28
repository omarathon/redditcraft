package dev.omarathon.redditcraft.commands.handler;

import dev.omarathon.redditcraft.commands.lib.Command;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.helper.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public abstract class Handler extends Command {
    protected ConfigurationSection commandMessages;
    protected EndpointEngine endpointEngine;

    public Handler(String formalArg, Selector from) {
        super(formalArg, from);
        endpointEngine = from.getEndpointEngine();
        commandMessages = Config.getSection("messages.commands");
        for (String node : getPermission()) {
            commandMessages = commandMessages.getConfigurationSection(node);
        }
    }

    public abstract void handle(CommandSender sender, String[] args);
}
