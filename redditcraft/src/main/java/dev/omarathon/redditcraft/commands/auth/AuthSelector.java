package dev.omarathon.redditcraft.commands.auth;

import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.MainSelector;
import dev.omarathon.redditcraft.commands.auth.handlers.BeginHandler;
import dev.omarathon.redditcraft.commands.auth.handlers.ConfirmHandler;
import dev.omarathon.redditcraft.commands.auth.handlers.ConfirmQueue;
import dev.omarathon.redditcraft.commands.auth.handlers.StatusHandler;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;

public class AuthSelector extends Selector {
    private AuthManager authManager;
    private FlairManager flairManager;

    public AuthSelector(MainSelector from) {
        super("authentication", from);
        authManager = from.getAuthManager();
        flairManager = from.getFlairManager();
        addArg("auth");
        addArg("authenticate");
        ConfirmQueue confirmQueue = new ConfirmQueue();
        bind(new BeginHandler(this, confirmQueue));
        bind(new StatusHandler(this));
        bind(new ConfirmHandler(this, confirmQueue));
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public FlairManager getFlairManager() {
        return flairManager;
    }
}
