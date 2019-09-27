package dev.omarathon.redditcraft.data.endpoints;

import dev.omarathon.redditcraft.helper.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public enum FlairStatus {
    ON (2, "on", "&aon"),
    OFF (1, "off", "&coff"),
    NULL (0,"null", "&cremoved");

    private String configKey;
    private String friendlyName;
    private int internalInt;

    FlairStatus(int internalInt, String configKey, String friendlyName) {
        this.configKey = configKey;
        this.friendlyName = friendlyName;
        this.internalInt = internalInt;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getMessage(boolean admin) {
        ConfigurationSection configurationSection = Config.getSection("messages.flair.status");
        if (admin) {
            configurationSection = configurationSection.getConfigurationSection("admin");
        }
        return configurationSection.getString(configKey);
    }

    public int getInternalInt() {
        return internalInt;
    }

    @NotNull
    public static FlairStatus fromInternalInt(int internalInt) throws IllegalArgumentException {
        for (FlairStatus flairStatus : FlairStatus.values()) {
            if (flairStatus.getInternalInt() == internalInt) {
                return flairStatus;
            }
        }
        throw new IllegalArgumentException();
    }
}
