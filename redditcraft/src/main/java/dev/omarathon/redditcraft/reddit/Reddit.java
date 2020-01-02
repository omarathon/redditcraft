package dev.omarathon.redditcraft.reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.AccountStatus;
import net.dean.jraw.models.Message;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.references.SubredditReference;

public class Reddit {
    private static RedditClient redditClient;

    public static boolean userExists(String username) {
        return (redditClient.user(username).query().getStatus() == AccountStatus.EXISTS);
    }

    public static void setLogging(boolean log) {
        redditClient.setLogHttp(log);
    }

    public static RedditClient getRedditClient() {
        return redditClient;
    }

    public static void setRedditClient(RedditClient redditClient) {
        Reddit.redditClient = redditClient;
    }

    public static BarebonesPaginator<Message> getUnreadMessages() {
        return redditClient.me().inbox().iterate("unread").build();
    }

    public static void setMessageRead(Message message) {
        redditClient.me().inbox().markRead(true, message.getFullName());
    }

    public static String getBotUsername() {
        return redditClient.me().getUsername();
    }

    // obtains SubredditReference object from input name.
    public static SubredditReference getSubreddit(String name) {
        return redditClient.subreddit(name);
    }
}
