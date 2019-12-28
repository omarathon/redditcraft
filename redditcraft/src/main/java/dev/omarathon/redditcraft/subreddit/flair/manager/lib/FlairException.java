package dev.omarathon.redditcraft.subreddit.flair.manager.lib;

public class FlairException extends RuntimeException {
    public enum Kind {
        LENGTH_EXCEEDED {
            @Override
            public String toString() {
                return "Flair too long!";
            }
        },

        PLAYER_NOT_EXIST {
            @Override
            public String toString() {
                return "Minecraft player doesn't exist!";
            }
        },

        PLAYER_NOT_ONLINE,

        NO_FOUND_REDDIT_USERNAME,

        FLAIR_NOT_EXIST {
            @Override
            public String toString() {
                return "Flair doesn't exist!";
            }
        },

        PLAYER_LOAD_ERROR {
            @Override
            public String toString() {
                return "Player failed to load - may not exist!";
            }
        };

        public String toString() {
            return "Flair exception.";
        }
    }

    public FlairException(Kind exceptionKind) {
        super(exceptionKind.toString());
    }
}
