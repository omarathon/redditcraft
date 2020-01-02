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

        PREFIX_LOAD_TIMEOUT {
            @Override
            public String toString() {
                return ("Flair exception: Lookup of player prefix timed out!");
            }
        },

        PREFIX_LOAD_ERROR {
            @Override
            public String toString() {
                return "Flair exception: Failed to obtain player prefix!";
            }
        },

        PERMISSION_CHECK_TIMEOUT {
            @Override
            public String toString() {
                return "Flair exception: Permission check timed out!";
            }
        },

        PERMISSION_CHECK_ERROR {
            @Override
            public String toString() {
                return "Flair exception: Failed to perform permission check!";
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
