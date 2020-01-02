package dev.omarathon.redditcraft.subreddit.flair.manager.presets;

import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.helper.VaultAsync;
import dev.omarathon.redditcraft.subreddit.SubredditManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairData;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PrefixSyncFlairManager extends FlairManager {
    private String flairId;
    private int offlinePrefixLookupTimeoutMs;

    public PrefixSyncFlairManager(SubredditManager subredditManager) {
        super(subredditManager);
        flairId = flairsConfigSection.getString("custom-flairs-id");
        offlinePrefixLookupTimeoutMs = flairsConfigSection.getInt("offline-prefix-lookup-timeout-ms");
    }

    @Override
    protected FlairData getFlair(OfflinePlayer offlinePlayer, int charLimit) throws FlairException {
        String prefix = "";
        if (charLimit > 0) {
            try {
                prefix = VaultAsync.getPrefix(offlinePlayer, subredditManager.getChat(), offlinePrefixLookupTimeoutMs, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
                if (e instanceof TimeoutException) {
                    throw new FlairException(FlairException.Kind.PREFIX_LOAD_TIMEOUT);
                }
                e.printStackTrace();
                throw new FlairException(FlairException.Kind.PREFIX_LOAD_ERROR);
            }
            if (prefix == null) {
                throw new FlairException(FlairException.Kind.PREFIX_LOAD_ERROR);
            }
            prefix = prefix.replaceAll("(&[0-9A-Fa-fK-Ok-oRr])*", "");
            int length = prefix.length();
            if (length > charLimit) {
                prefix = prefix.substring(0, charLimit);
            }
        }
        return new FlairData(flairId, prefix);
    }
}
