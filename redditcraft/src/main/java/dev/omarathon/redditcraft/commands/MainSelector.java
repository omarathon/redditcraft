package dev.omarathon.redditcraft.commands;

import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.admin.AdminSelector;
import dev.omarathon.redditcraft.commands.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.flair.FlairSelector;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;

public class MainSelector extends Selector {
    public MainSelector(AuthManager authManager, FlairManager flairManager, EndpointEngine endpointEngine) {
        super("redditcraft", endpointEngine, authManager, flairManager);
        bind(new AdminSelector(this));
        bind(new AuthSelector(this));
        bind(new FlairSelector(this));
    }
}
