package dev.omarathon.redditcraft.auth.verifier;

public enum VerificationResult {
    SUCCESS ("success"),
    FAIL ("failure"),
    SUCCESS_NO_FLAIR ("success-but-flair-failed"),
    ERROR ("error");

    private String configMessageKey;

    VerificationResult(String configMessageKey) {
        this.configMessageKey = configMessageKey;
    }

    public String getConfigMessageKey() {
        return configMessageKey;
    }
}
