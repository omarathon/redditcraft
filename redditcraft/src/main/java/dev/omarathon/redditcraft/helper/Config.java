package dev.omarathon.redditcraft.helper;

import dev.omarathon.redditcraft.RedditCraft;
import dev.omarathon.redditcraft.data.engines.presets.sql.connection.SQLConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class Config {
    private static FileConfiguration config;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        config = RedditCraft.getInstance().getConfig();
    }

    public static SQLConfiguration getSQLConfiguation() {
        ConfigurationSection sqlSection = config.getConfigurationSection("mysql");
        return new SQLConfiguration(
                sqlSection.getString("host"),
                sqlSection.getInt("port"),
                sqlSection.getString("database"),
                sqlSection.getString("table-prefix"),
                sqlSection.getString("username"),
                sqlSection.getString("password")
        );
    }

    public static ConfigurationSection getSection(String key) {
        return config.getConfigurationSection(key);
    }

    public static String fillPlaceholders(String message, Map<String, String> placeholders) {
        for (String placeholder : placeholders.keySet()) {
            message = fillPlaceholder(message, placeholder, placeholders.get(placeholder));
        }
        return message;
    }

    public static String fillPlaceholder(String message, String placeholder, String value) {
        return message.replaceAll("<" + placeholder + ">", value);
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}
