package dev.omarathon.redditcraft.commands.flair;

import dev.omarathon.redditcraft.commands.MainSelector;
import dev.omarathon.redditcraft.commands.flair.handlers.*;
import dev.omarathon.redditcraft.commands.selector.Selector;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;

public class FlairSelector extends Selector {
    private FlairManager flairManager;

    public FlairSelector(MainSelector from) {
        super("flair", from);
        this.flairManager = from.getFlairManager();
        addArg("prefix");
        bind(new OnHandler(this));
        bind(new OffHandler(this));
        bind(new UpdateHandler(this));
        bind(new StatusHandler(this));
        bind(new RemoveHandler(this));
    }

    public FlairManager getFlairManager() {
        return flairManager;
    }
}
