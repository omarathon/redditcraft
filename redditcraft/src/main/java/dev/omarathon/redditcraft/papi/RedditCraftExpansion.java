package dev.omarathon.redditcraft.papi;

import dev.omarathon.redditcraft.RedditCraft;
import dev.omarathon.redditcraft.data.endpoints.RedditUsernameAuthenticatedPair;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Error;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

public class RedditCraftExpansion extends PlaceholderExpansion {
    private RedditCraft redditCraft;
    private ConfigurationSection placeholdersConfigSection;

    public RedditCraftExpansion(RedditCraft redditCraft){
        this.redditCraft = redditCraft;
        placeholdersConfigSection = Config.getSection("placeholders");
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor(){
        return redditCraft.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "redditcraft";
    }

    @Override
    public String getVersion(){
        return redditCraft.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier){
        if (identifier.equals("authenticatedredditusername")) {
            ConfigurationSection configurationSection = placeholdersConfigSection.getConfigurationSection("authenticatedredditusername");
            RedditUsernameAuthenticatedPair redditUsernameAuthenticatedPair;
            try {
                redditUsernameAuthenticatedPair = redditCraft.getEndpointEngine().getRedditUsernameAuthenticatedPair(offlinePlayer.getUniqueId());
            }
            catch (Exception e) {
                Error.handleException(Bukkit.getConsoleSender(), e);
                return configurationSection.getString("error");
            }
            if (redditUsernameAuthenticatedPair == null || !redditUsernameAuthenticatedPair.isAuthenticated()) {
                return configurationSection.getString("none");
            }
            return redditUsernameAuthenticatedPair.getRedditUsername();
        }

        return null;
    }
}
