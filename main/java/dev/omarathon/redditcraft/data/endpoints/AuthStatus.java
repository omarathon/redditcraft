package dev.omarathon.redditcraft.data.endpoints;

import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.helper.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum AuthStatus {
    NOT_EXISTING ("not-existing", "USER NOT EXIST") {
        @Override @NotNull
        public Map<String, String> getConfigMessagePlaceholders(UUID uuid, EndpointEngine endpointEngine) {
            return Collections.emptyMap();
        }
    },

    NEW ("new", "NEW") {
        @Override @NotNull
        public Map<String, String> getConfigMessagePlaceholders(UUID uuid, EndpointEngine endpointEngine) {
            Map<String, String> map = new HashMap<>(1);
            map.put("username", endpointEngine.getRedditUsername(uuid));
            return map;
        }
    },

    IN_PROGRESS ("in-progress", "IN PROGRESS") {
        @Override @NotNull
        public Map<String, String> getConfigMessagePlaceholders(UUID uuid, EndpointEngine endpointEngine) {
            Map<String, String> map = new HashMap<>(1);
            map.put("username", endpointEngine.getRedditUsername(uuid));
            LocalDateTime expiry = endpointEngine.getAuthExpiry(uuid);
            map.put("time-left", Long.toString(ChronoUnit.MINUTES.between(LocalDateTime.now(), expiry)));
            return map;
        }
    },

    NEW_FAILED ("new-failed", "NEW (RECENTLY FAILED)") {
        @Override @NotNull
        public Map<String, String> getConfigMessagePlaceholders(UUID uuid, EndpointEngine endpointEngine) {
            Map<String, String> map = new HashMap<>(1);
            map.put("username", endpointEngine.getRedditUsername(uuid));
            return map;
        }
    },

    AUTHENTICATED ("authenticated", "AUTHENTICATED") {
        @Override @NotNull
        public Map<String, String> getConfigMessagePlaceholders(UUID uuid, EndpointEngine endpointEngine) {
            Map<String, String> map = new HashMap<>(1);
            map.put("username", endpointEngine.getRedditUsername(uuid));
            return map;
        }
    };

    private String configKey;
    private String friendlyName;

    AuthStatus(@NotNull String configKey, @NotNull String friendlyName) {
        this.configKey = configKey;
        this.friendlyName = friendlyName;
    }

    @NotNull
    public String getConfigKey() {
        return configKey;
    }

    @NotNull
    public String getFriendlyName() {
        return friendlyName;
    }

    @NotNull
    public abstract Map<String, String> getConfigMessagePlaceholders(UUID uuid, EndpointEngine endpointEngine);

    public String getMessage(boolean admin, UUID uuid, EndpointEngine endpointEngine) {
        ConfigurationSection configurationSection = Config.getSection("messages.auth.status");
        if (admin) {
            configurationSection = configurationSection.getConfigurationSection("admin");
        }
        String message = configurationSection.getString(getConfigKey());
        return Config.fillPlaceholders(message, getConfigMessagePlaceholders(uuid, endpointEngine));
    }
}
