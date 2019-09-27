package dev.omarathon.redditcraft.helper;

import dev.omarathon.ambientmessenger.AmbientMessenger;
import dev.omarathon.redditcraft.RedditCraft;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class Messaging {
    private static AmbientMessenger ambientMessenger = null;

    public static void setAmbientMessenger(AmbientMessenger ambientMessenger) {
        Messaging.ambientMessenger = ambientMessenger;
    }

    public static AmbientMessenger getAmbientMessenger() {
        return ambientMessenger;
    }

    // attaches the prefix, and sends the input message to the player
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendPrefixedAmbientMessage(OfflinePlayer recipient, String message, Timestamp expiry) throws SQLException {
        sendAmbientMessage(recipient, addPrefix(message), expiry);
    }

    public static void sendAmbientMessage(OfflinePlayer recipient, String message, Timestamp expiry) throws SQLException {
        ambientMessenger.sendMessage(recipient, ChatColor.translateAlternateColorCodes('&', message), expiry);
    }

    public static void sendPrefixedMessage(CommandSender sender, String message) {
        sendMessage(sender, addPrefix(message));
    }

    public static String addPrefix(String message) {
        return Config.getSection("messages").getString("prefix") + message;
    }

    public static void notifyError(CommandSender sender) {
        sendPrefixedMessage(sender, Config.getSection("messages.general").getString("error"));
    }

    public static Logger getLogger() {
        return RedditCraft.getInstance().getLogger();
    }
}
