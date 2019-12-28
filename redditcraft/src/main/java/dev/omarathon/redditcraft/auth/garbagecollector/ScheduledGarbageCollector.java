package dev.omarathon.redditcraft.auth.garbagecollector;

import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.RepeatingTask;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

public class ScheduledGarbageCollector extends RepeatingTask {
    private GarbageCollector garbageCollector;

    public ScheduledGarbageCollector(GarbageCollector garbageCollector) {
        super(Config.getSection("auth").getLong("garbage-collector-cooldown-hours"), TimeUnit.HOURS);
        this.garbageCollector = garbageCollector;
    }

    @Override
    public void run() {
        garbageCollector.run(Bukkit.getConsoleSender());
    }
}
