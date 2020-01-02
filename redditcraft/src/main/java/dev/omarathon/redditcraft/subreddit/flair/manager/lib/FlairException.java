package dev.omarathon.redditcraft.subreddit.flair.manager.lib;

public class FlairException extends RuntimeException {
    public enum Kind {
        LENGTH_EXCEEDED {
            @Override
            public String toString() {
                return "Flair exception: Flair too long!";
            }
        },

        PLAYER_NOT_EXIST {
            @Override
            public String toString() {
                return "Flair exception: Minecraft player doesn't exist!";
            }
        },

        PLAYER_NOT_ONLINE{
            @Override
            public String toString() {
                return "Flair exception: Player not online!";
            }
        },

        NO_FOUND_REDDIT_USERNAME {
            @Override
            public String toString() {
                return "Flair exception: No found reddit username!";
            }
        },

        FLAIR_NOT_EXIST {
            @Override
            public String toString() {
                return "Flair exception: Flair doesn't exist!";
            }
        },

        PLAYER_LOAD_ERROR {
            @Override
            public String toString() {
                return "Flair exception: Player failed to load - may not exist!";
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
