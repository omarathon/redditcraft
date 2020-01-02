package dev.omarathon.redditcraft.helper.async;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;

public class PermissionRunnable implements Runnable {
    private OfflinePlayer offlinePlayer;
    private String permission;
    private Permission perms;
    private boolean has;

    public PermissionRunnable(OfflinePlayer offlinePlayer, String permission, Permission perms) {
        this.offlinePlayer = offlinePlayer;
        this.permission = permission;
        this.perms = perms;
        this.has = false;
    }

    @Override
    public void run() {
        has = perms.playerHas(null, offlinePlayer, permission);
    }

    public boolean hasPermission() {
        return has;
    }
}
