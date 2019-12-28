package dev.omarathon.redditcraft.commands.auth.handlers;

import dev.omarathon.redditcraft.helper.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfirmQueue {
    private Map<UUID, String> uuidRedditUsernameWindow;
    private long windowSeconds;

    public ConfirmQueue() {
        windowSeconds = Config.getSection("auth").getLong("confirmation-window-seconds");
        uuidRedditUsernameWindow = new HashMap<>();
    }

    public void addToWindow(UUID playerUUID, String redditUsername) {
        uuidRedditUsernameWindow.put(playerUUID, redditUsername);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Runnable remove = () -> {
            uuidRedditUsernameWindow.remove(playerUUID);
        };
        scheduledExecutorService.schedule(remove, windowSeconds, TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
    }

    public void removeFromWindow(UUID playerUUID) {
        uuidRedditUsernameWindow.remove(playerUUID);
    }

    public Map<UUID, String> getUuidRedditUsernameWindow() {
        return uuidRedditUsernameWindow;
    }
}
