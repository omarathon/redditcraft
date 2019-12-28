package dev.omarathon.redditcraft.subreddit.flair.manager.presets;

import dev.omarathon.redditcraft.subreddit.SubredditManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairData;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import org.bukkit.OfflinePlayer;

public class PrefixSyncFlairManager extends FlairManager {
    private String flairId;

    public PrefixSyncFlairManager(SubredditManager subredditManager) {
        super(subredditManager);
        flairId = flairsConfigSection.getString("custom-flairs-id");
    }

    @Override
    protected FlairData getFlair(OfflinePlayer offlinePlayer, int charLimit) throws FlairException {
        String prefix = "";
        if (charLimit > 0) {
            prefix = obtainPrefix(offlinePlayer).replaceAll("(&[0-9A-Fa-fK-Ok-oRr])*", "");
            int length = prefix.length();
            if (length > charLimit) {
                prefix = prefix.substring(0, charLimit);
            }
        }
        return new FlairData(flairId, prefix);
    }

    private String obtainPrefix(OfflinePlayer offlinePlayer) throws FlairException {
        LuckPermsApi api = LuckPerms.getApi();
        if (!api.getStorage().loadUser(offlinePlayer.getUniqueId()).join()) {
            // got an error whilst loading the user
            throw new FlairException(FlairException.Kind.PLAYER_LOAD_ERROR);
        }
        User user = api.getUser(offlinePlayer.getUniqueId());
        if (user == null) {
            throw new FlairException(FlairException.Kind.PLAYER_LOAD_ERROR);
        }

        Contexts contexts = Contexts.allowAll();
        MetaData metaData = user.getCachedData().getMetaData(contexts);
        return metaData.getPrefix();
    }
}
