package dev.omarathon.redditcraft.subreddit.flair.manager.presets;

import dev.omarathon.redditcraft.subreddit.SubredditManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairData;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
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
        LuckPerms api = LuckPermsProvider.get();
        if (!api.getStorage().loadUser(offlinePlayer.getUniqueId()).join()) { // TODO: fix obtaining offline users
            // got an error whilst loading the user
            throw new FlairException(FlairException.Kind.PLAYER_LOAD_ERROR);
        }
        User user = api.getUserManager().getUser(offlinePlayer.getUniqueId());
        if (user == null) {
            throw new FlairException(FlairException.Kind.PLAYER_LOAD_ERROR);
        }

        ContextManager cm = api.getContextManager();
        ImmutableContextSet contextSet = cm.getContext(user).orElse(cm.getStaticContext());
        QueryOptions queryOptions = cm.getQueryOptions(user).orElse(cm.getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);
        return metaData.getPrefix();
    }
}
