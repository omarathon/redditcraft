package dev.omarathon.redditcraft.commands.admin.auth.garbage;

import dev.omarathon.redditcraft.auth.garbagecollector.GarbageCollector;
import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.selector.Selector;

public class GarbageSelector extends Selector {
    private GarbageCollector garbageCollector;

    public GarbageSelector(AuthSelector from) {
        super("garbagecollector", from);
        garbageCollector = from.getAuthManager().getGarbageCollector();
        addArg("garbage");
        bind(new RunHandler(this));
    }

    public GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }
}
