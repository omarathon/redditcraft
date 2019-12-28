package dev.omarathon.redditcraft.auth.verifier;

import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.RepeatingTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.TimeUnit;

public class ScheduledVerifier extends RepeatingTask {
    private String authMessageSubject;
    private Verifier verifier;

    public ScheduledVerifier(Verifier verifier) {
       this(verifier, Config.getSection(  "auth.verification"));
    }

    private ScheduledVerifier(Verifier verifier, ConfigurationSection configurationSection) {
        super(configurationSection.getLong("cooldown-seconds"), TimeUnit.SECONDS);
        this.verifier = verifier;
        authMessageSubject = configurationSection.getString("reddit-message-subject");
    }

    @Override
    public void run() {
        verifier.run(Bukkit.getConsoleSender(), true);
    }
}
