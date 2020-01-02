package dev.omarathon.redditcraft.helper;

import dev.omarathon.redditcraft.helper.async.PermissionRunnable;
import dev.omarathon.redditcraft.helper.async.PrefixRunnable;
import dev.omarathon.redditcraft.helper.async.TimeLimitedCodeBlock;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.TimeUnit;

public class VaultAsync {
    public static String getPrefix(OfflinePlayer offlinePlayer, Chat chat, long timeout, TimeUnit timeoutUnit) throws Exception {
        PrefixRunnable runnable = new PrefixRunnable(offlinePlayer, chat);
        TimeLimitedCodeBlock.runWithTimeout(runnable, timeout, timeoutUnit);
        return runnable.getPrefix();
    }

    public static boolean hasPermission(OfflinePlayer offlinePlayer, String permisssion, Permission perms, long timeout, TimeUnit timeoutUnit) throws Exception {
        PermissionRunnable runnable = new PermissionRunnable(offlinePlayer, permisssion, perms);
        TimeLimitedCodeBlock.runWithTimeout(runnable, timeout, timeoutUnit);
        return runnable.hasPermission();
    }
}
