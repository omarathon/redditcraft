package dev.omarathon.redditcraft.commands.lib;

import dev.omarathon.redditcraft.data.EndpointEngine;

public class EndpointCommand extends Command {
    private EndpointEngine endpointEngine;

    public EndpointCommand(String formalArg, EndpointCommand parent) {
        super(formalArg, parent);
        endpointEngine = parent.getEndpointEngine();
    }

    public EndpointEngine getEndpointEngine() {
        return endpointEngine;
    }
}
