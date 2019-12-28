package dev.omarathon.redditcraft.data.endpoints;

public class RedditUsernameAuthenticatedPair {
    private String redditUsername;
    private boolean authenticated;

    public RedditUsernameAuthenticatedPair(String redditUsername, boolean authenticated) {
        this.redditUsername = redditUsername;
        this.authenticated = authenticated;
    }

    public String getRedditUsername() {
        return redditUsername;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
