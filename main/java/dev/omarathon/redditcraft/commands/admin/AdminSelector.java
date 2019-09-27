package dev.omarathon.redditcraft.commands.admin;

import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.MainSelector;
import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.admin.flair.FlairSelector;
import dev.omarathon.redditcraft.commands.admin.handlers.ReloadHandler;
import dev.omarathon.redditcraft.commands.admin.handlers.ResetHandler;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;

public class AdminSelector extends Selector {
    private AuthManager authManager;
    private FlairManager flairManager;

    public AdminSelector(MainSelector from) {
        super("admin", from);
        authManager = from.getAuthManager();
        flairManager = from.getFlairManager();
        bind(new AuthSelector(this));
        bind(new FlairSelector(this));
        bind(new ReloadHandler(this));
        bind(new ResetHandler(this));
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public FlairManager getFlairManager() {
        return flairManager;
    }
}
