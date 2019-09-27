package dev.omarathon.redditcraft.subreddit.flair.manager.lib;

import dev.omarathon.redditcraft.data.EndpointEngine;
import net.dean.jraw.references.SubredditReference;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FlairData {
    private String id;
    private String text;

    public FlairData(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void applyToMinecraftUser(@NotNull UUID uuid, @NotNull SubredditReference subreddit, EndpointEngine endpointEngine) throws FlairException {
        String redditUsername = endpointEngine.getRedditUsername(uuid);
        if (redditUsername == null) {
            throw new FlairException(FlairException.Kind.NO_FOUND_REDDIT_USERNAME);
        }
        applyToRedditUser(redditUsername, subreddit);
    }

    public void applyToRedditUser(@NotNull String redditUsername, @NotNull SubredditReference subreddit) {
        subreddit.otherUserFlair(redditUsername).updateToTemplate(id, text);
    }
}
