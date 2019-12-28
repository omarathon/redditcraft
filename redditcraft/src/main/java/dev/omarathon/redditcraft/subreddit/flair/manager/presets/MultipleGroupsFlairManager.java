package dev.omarathon.redditcraft.subreddit.flair.manager.presets;

import dev.omarathon.redditcraft.subreddit.SubredditManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairData;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import net.dean.jraw.models.Flair;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MultipleGroupsFlairManager extends FlairManager {
    private String delimiter;
    private int delimiterLength;
    private ConfigurationSection groups;
    private ConfigurationSection flairs;
    private List<String> groupPriorities;
    private String flairId;

    private Flair opFlair;
    private int opFlairTextLength;

    public MultipleGroupsFlairManager(SubredditManager subredditManager) throws FlairException {
        super(subredditManager);
        flairId = flairsConfigSection.getString("custom-flairs-id");
        if (!flairMap.containsKey(flairId)) {
            throw new FlairException(FlairException.Kind.FLAIR_NOT_EXIST);
        }
        ConfigurationSection configSection = flairsConfigSection.getConfigurationSection("multiplegroups-config");
        delimiter = configSection.getString("delimiter");
        delimiterLength = delimiter.length();
        groups = configSection.getConfigurationSection("groups");
        flairs = configSection.getConfigurationSection("flairs");
        groupPriorities = configSection.getStringList("group-priorities");

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
        List<String> flairsList = new ArrayList<>();
        int completeFlairLength = 0;
        if (charLimit > 0) {
            // require online player because need to do permission checks
            Player player = offlinePlayer.getPlayer();
            if (player == null) {
                throw new FlairException(FlairException.Kind.PLAYER_NOT_ONLINE);
            }
            boolean iterateGroups = true;
            for (String group : groupPriorities) {
                for (String flair : groups.getStringList(group)) {
                    ConfigurationSection flairData = flairs.getConfigurationSection(flair);
                    if (player.hasPermission(flairData.getString("requires-permission"))) {
                        String flairText = flairData.getString("flair-text");
                        int flairTextLength = flairText.length();
                        if (flairsList.size() == 0) {
                            if (flairTextLength <= charLimit) {
                                flairsList.add(flairText);
                                completeFlairLength = flairTextLength;
                            }
                            else {
                                iterateGroups = false;
                            }
                        }
                        else { // not first flair, so check insertion of delimiter too
                            int flairAndDelimiterLength = flairTextLength + delimiterLength;
                            if (completeFlairLength + flairAndDelimiterLength <= charLimit) {
                                flairsList.add(flairText);
                                completeFlairLength += flairAndDelimiterLength;
                            }
                            else {
                                iterateGroups = false;
                            }
                        }
                        break;
                    }
                }
                if (!iterateGroups) {
                    break;
                }
            }
        }
        return new FlairData(flairId, String.join(delimiter, flairsList));
    }
}
