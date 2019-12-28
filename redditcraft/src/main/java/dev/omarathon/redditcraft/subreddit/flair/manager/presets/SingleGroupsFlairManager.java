package dev.omarathon.redditcraft.subreddit.flair.manager.presets;

import dev.omarathon.redditcraft.subreddit.SubredditManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairData;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import net.dean.jraw.models.Flair;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class SingleGroupsFlairManager extends FlairManager {
    private List<String> flairPriorities;
    private ConfigurationSection flairs;

    private Flair defaultFlair;
    private int defaultFlairTextLength;

    private Flair opFlair;
    private int opFlairTextLength;

    public SingleGroupsFlairManager(SubredditManager subredditManager) throws FlairException {
        super(subredditManager);
        ConfigurationSection configSection = flairsConfigSection.getConfigurationSection("singlegroups-config");
        flairPriorities = configSection.getStringList("flair-priorities");
        flairs = configSection.getConfigurationSection("flairs");
        for (String flair : flairs.getKeys(false)) {
            if (!flairMap.containsKey(flairs.getConfigurationSection(flair).getString("id"))) {
                throw new FlairException(FlairException.Kind.FLAIR_NOT_EXIST);
            }
        }
        defaultFlair = flairMap.get(configSection.getString("default-flair-id"));
        if (defaultFlair == null) throw new FlairException(FlairException.Kind.FLAIR_NOT_EXIST);
        defaultFlairTextLength = defaultFlair.getText().length();

        opFlair = flairMap.get(configSection.getString("op-flair-id"));
        if (opFlair == null) throw new FlairException(FlairException.Kind.FLAIR_NOT_EXIST);
        opFlairTextLength = opFlair.getText().length();
    }

    @Override
    protected FlairData getFlair(OfflinePlayer offlinePlayer, int charLimit) throws FlairException {
        if (offlinePlayer.isOp()) {
            if (opFlairTextLength > charLimit) {
                throw new FlairException(FlairException.Kind.LENGTH_EXCEEDED);
            }
            return new FlairData(opFlair.getId(), opFlair.getText());
        }
        for (String flair : flairPriorities) {
            ConfigurationSection flairData = flairs.getConfigurationSection(flair);
            Player player = offlinePlayer.getPlayer();
            if (player == null) {
                throw new FlairException(FlairException.Kind.PLAYER_NOT_ONLINE);
            }
            if (player.hasPermission(flairData.getString("requires-permission"))) {
                String flairId = flairData.getString("id");
                if (flairMap.get(flairId).getText().length() > charLimit) {
                    throw new FlairException(FlairException.Kind.LENGTH_EXCEEDED);
                }
                return new FlairData(flairId, flairMap.get(flairId).getText());
            }
        }
        if (defaultFlairTextLength > charLimit) {
            throw new FlairException(FlairException.Kind.LENGTH_EXCEEDED);
        }
        return new FlairData(defaultFlair.getId(), defaultFlair.getText());
    }
}
