package dev.omarathon.redditcraft.commands.admin.auth;

import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.admin.AdminSelector;
import dev.omarathon.redditcraft.commands.admin.auth.garbage.GarbageSelector;
import dev.omarathon.redditcraft.commands.admin.auth.handlers.*;
import dev.omarathon.redditcraft.commands.admin.auth.verification.VerificationSelector;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;

public class AuthSelector extends Selector {
    private AuthManager authManager;
    private FlairManager flairManager;

    public AuthSelector(AdminSelector from) {
        super("authentication", from);
        addArg("auth");
        authManager = from.getAuthManager();
        flairManager = from.getFlairManager();
        bind(new GarbageSelector(this));
        bind(new VerificationSelector(this));
        bind(new StatusHandler(this));
        bind(new ForceHandler(this));
        bind(new VoidHandler(this));
        bind(new InvalidateTokensHandler(this));
        bind(new ResolveHandler(this));
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public FlairManager getFlairManager() {
        return flairManager;
    }
}
