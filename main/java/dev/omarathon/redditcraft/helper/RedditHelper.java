package dev.omarathon.redditcraft.helper;

import java.util.regex.Pattern;

public class RedditHelper {
    public static boolean validUsername(String username) {
        return Pattern.matches("[A-Za-z0-9+_-]{3,20}", username);
    }
}
