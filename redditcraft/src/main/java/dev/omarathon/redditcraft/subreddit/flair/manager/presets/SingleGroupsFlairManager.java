package dev.omarathon.redditcraft.subreddit.flair.manager.presets;

import dev.omarathon.redditcraft.helper.VaultAsync;
import dev.omarathon.redditcraft.subreddit.SubredditManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairData;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import net.dean.jraw.models.Flair;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SingleGroupsFlairManager extends FlairManager {
    private List<String> flairPriorities;
    private ConfigurationSection flairs;

    private Flair defaultFlair;
    private int defaultFlairTextLength;

    private Flair opFlair;
    private int opFlairTextLength;

    private int offlinePermissionLookupTimeoutMs;

    public SingleGroupsFlairManager(SubredditManager subredditManager) throws FlairException {
        super(subredditManager);
        ConfigurationSection configSection = flairsConfigSection.getConfigurationSection("singlegroups-config");
        offlinePermissionLookupTimeoutMs = flairsConfigSection.getInt("offline-permission-node-lookup-timeout-ms");
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
            boolean hasPerm;
            try {
                hasPerm = VaultAsync.hasPermission(offlinePlayer, flairData.getString("requires-permission"), subredditManager.getPerm(), offlinePermissionLookupTimeoutMs, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
                if (e instanceof TimeoutException) {
                    throw new FlairException(FlairException.Kind.PERMISSION_CHECK_TIMEOUT);
                }
                throw new FlairException(FlairException.Kind.PERMISSION_CHECK_ERROR);
            }
            if (hasPerm) {
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
