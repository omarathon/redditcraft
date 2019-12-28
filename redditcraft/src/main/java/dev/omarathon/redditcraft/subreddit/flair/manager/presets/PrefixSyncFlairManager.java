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
import org.bukkit.entity.Player;

public class PrefixSyncFlairManager extends FlairManager {
    private String flairId;

    public PrefixSyncFlairManager(SubredditManager subredditManager) {
        super(subredditManager);
        flairId = flairsConfigSection.getString("custom-flairs-id");
    }

    // TODO: work with OfflinePlayers
    @Override
    protected FlairData getFlair(OfflinePlayer offlinePlayer, int charLimit) throws FlairException { // currently only works with online players!!
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            throw new FlairException(FlairException.Kind.PLAYER_NOT_ONLINE);
        }
        String prefix = "";
        if (charLimit > 0) {
            prefix = obtainPrefix(player).replaceAll("(&[0-9A-Fa-fK-Ok-oRr])*", "");
            int length = prefix.length();
            if (length > charLimit) {
                prefix = prefix.substring(0, charLimit);
            }
        }
        return new FlairData(flairId, prefix);
    }

    private String obtainPrefix(Player player) throws FlairException {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            throw new FlairException(FlairException.Kind.PLAYER_LOAD_ERROR);
        }
        ContextManager cm = api.getContextManager();
        QueryOptions queryOptions = cm.getQueryOptions(user).orElse(cm.getStaticQueryOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);
        return metaData.getPrefix();
    }
}
