package dev.omarathon.redditcraft.auth;

import dev.omarathon.redditcraft.auth.garbagecollector.GarbageCollector;
import dev.omarathon.redditcraft.auth.garbagecollector.ScheduledGarbageCollector;
import dev.omarathon.redditcraft.auth.verifier.ScheduledVerifier;
import dev.omarathon.redditcraft.auth.verifier.Verifier;

public class ScheduledServices {
    private ScheduledGarbageCollector garbageCollector;
    private ScheduledVerifier verifier;

    public ScheduledServices(GarbageCollector garbageCollector, Verifier verifier) {
        this.garbageCollector = new ScheduledGarbageCollector(garbageCollector);
        this.verifier = new ScheduledVerifier(verifier);
    }

    public void run() {
        garbageCollector.begin();
        verifier.begin();
    }

    public void stop() {
        garbageCollector.stop();
        verifier.stop();
    }

    public ScheduledGarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    public ScheduledVerifier getVerifier() {
        return verifier;
    }
}
