package dev.omarathon.redditcraft.helper.async;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.OfflinePlayer;

public class PrefixRunnable implements Runnable {
    private String prefix;
    private OfflinePlayer offlinePlayer;
    private Chat chat;

    public PrefixRunnable(OfflinePlayer offlinePlayer, Chat chat) {
        this.prefix = null;
        this.offlinePlayer = offlinePlayer;
        this.chat = chat;
    }

    @Override
    public void run() {
        this.prefix = chat.getPlayerPrefix(null, offlinePlayer);
    }

    public String getPrefix() {
        return prefix;
    }
}
