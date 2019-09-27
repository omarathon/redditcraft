package dev.omarathon.redditcraft.commands.admin.flair;

import dev.omarathon.redditcraft.commands.admin.AdminSelector;
import dev.omarathon.redditcraft.commands.admin.flair.handlers.*;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;

public class FlairSelector extends Selector {
    private FlairManager flairManager;

    public FlairSelector(AdminSelector from) {
        super("flair", from);
        flairManager = from.getFlairManager();
        addArg("prefix");
        bind(new OnHandler(this));
        bind(new OffHandler(this));
        bind(new StatusHandler(this));
        bind(new UpdateHandler(this));
        bind(new RemoveHandler(this));
    }

    public FlairManager getFlairManager() {
        return flairManager;
    }
}
