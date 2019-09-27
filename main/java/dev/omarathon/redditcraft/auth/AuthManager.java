package dev.omarathon.redditcraft.auth;

import dev.omarathon.redditcraft.auth.garbagecollector.GarbageCollector;
import dev.omarathon.redditcraft.auth.verifier.Verifier;
import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;

public class AuthManager {
    private GarbageCollector garbageCollector;
    private Verifier verifier;

    private ScheduledServices scheduledServices;

    public AuthManager(FlairManager flairManager, EndpointEngine endpointEngine) {
        garbageCollector = new GarbageCollector(endpointEngine);
        verifier = new Verifier(flairManager, endpointEngine);

        scheduledServices = new ScheduledServices(garbageCollector, verifier);
    }

    public void runScheduledServices() {
        scheduledServices.run();
    }

    public void stopScheduledServices() {
        scheduledServices.stop();
    }

    public GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    public Verifier getVerifier() {
        return verifier;
    }

    public ScheduledServices getScheduledServices() {
        return scheduledServices;
    }
}
