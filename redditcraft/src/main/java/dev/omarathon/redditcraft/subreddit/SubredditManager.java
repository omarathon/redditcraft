package dev.omarathon.redditcraft.subreddit;

import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.reddit.Reddit;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.presets.MultipleGroupsFlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.presets.PrefixSyncFlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.presets.SingleGroupsFlairManager;
import net.dean.jraw.ApiException;
import net.dean.jraw.models.Flair;
import net.dean.jraw.references.SubredditReference;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;

public class SubredditManager {
    private ConfigurationSection subredditConfigSection;
    private SubredditReference subredditReference;
    private List<Flair> flairs;
    private HashMap<String, String> flairTextMap;
    private FlairManager flairManager;
    private EndpointEngine endpointEngine;
    private Chat chat;
    private Permission perm;

    // throws IllegalStateException if RedditClient isn't a mod on the subredditManager in the config,
    // IllegalStateException if flair mode in config doesn't exist (must be one of prefixsync, groups)
    // ApiException if subreddit in config doesn't exist / inaccessible
    public SubredditManager(EndpointEngine endpointEngine, Chat chat, Permission perm) throws IllegalArgumentException, IllegalStateException, ApiException {
        this.subredditConfigSection = Config.getSection("subreddit");
        this.endpointEngine = endpointEngine;
        this.chat = chat;
        this.perm = perm;
        String subredditName = subredditConfigSection.getString("name");
        subredditReference = Reddit.getSubreddit(subredditName);
        if (subredditReference.about().isUserModerator()) {
            flairs = subredditReference.userFlairOptions();
            populateFlairTextMap();
        }
        else {
            throw new IllegalArgumentException("Not a mod on subreddit in Config: " + subredditName);
        }

        String flairModeString = subredditConfigSection.getString("flairs.mode");
        switch (flairModeString) {
            case "prefixsync":
                flairManager = new PrefixSyncFlairManager(this);
                break;

            case "singlegroups":
                flairManager = new SingleGroupsFlairManager(this);
                break;

            case "multiplegroups":
                flairManager = new MultipleGroupsFlairManager(this);
                break;

            default:
                throw new IllegalStateException("Flair mode in Config doesn't exist: " + flairModeString);
        }
    }

    private void populateFlairTextMap() {
        flairTextMap = new HashMap<>();
        for (Flair flair : flairs) {
            flairTextMap.put(flair.getId(), flair.getText());
        }
    }

    public SubredditReference getSubredditReference() {
        return subredditReference;
    }

    public ConfigurationSection getSubredditConfigSection() {
        return subredditConfigSection;
    }

    public FlairManager getFlairManager() {
        return flairManager;
    }

    public EndpointEngine getEndpointEngine() {
        return endpointEngine;
    }

    public Chat getChat() {
        return chat;
    }

    public Permission getPerm() {
        return perm;
    }
}
