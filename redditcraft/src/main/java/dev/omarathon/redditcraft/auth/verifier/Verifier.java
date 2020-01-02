package dev.omarathon.redditcraft.auth.verifier;

import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.data.endpoints.FlairStatus;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.reddit.Reddit;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.pagination.BarebonesPaginator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

public class Verifier {
    private static ConfigurationSection settingsConfigSection = Config.getSection("auth.verification");
    private static ConfigurationSection messagesConfigSection = Config.getSection("messages.auth.verifier");

    private FlairManager flairManager;
    private EndpointEngine endpointEngine;

    public Verifier(FlairManager flairManager, EndpointEngine endpointEngine) {
        this.flairManager = flairManager;
        this.endpointEngine = endpointEngine;
    }


    public void run(CommandSender sender, boolean sendResults) {
        Messaging.getLogger().info("Running reddit verifier...");
        BarebonesPaginator<Message> unreadMessagePages = Reddit.getUnreadMessages();
        for (Listing<Message> unreadMessagePage : unreadMessagePages) {
            for (Message message : unreadMessagePage) {
                String result;
                if (message.getSubject().equals(settingsConfigSection.getString("reddit-message-subject"))) {
                    String username = message.getBody();
                    if (Pattern.matches("^[a-zA-Z0-9_]{3,16}$", username)) { // eliminate nonsense messages
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
                        UUID uuid = offlinePlayer.getUniqueId();
                        if (offlinePlayer.getName().equals(username)) { // existence check
                            VerificationResult verificationResult;
                            try {
                                if (endpointEngine.getRedditUsername(uuid).equals(message.getAuthor())) {
                                    if (endpointEngine.getAuthenticatedUuidsWithRedditUsername(message.getAuthor()).isEmpty()) {
                                        if (LocalDateTime.now().isBefore(endpointEngine.getAuthExpiry(uuid))) {
                                            verificationResult = verify(offlinePlayer);
                                        }
                                        else {
                                            verificationResult = VerificationResult.TOKEN_EXPIRED;
                                        }
                                    }
                                    else {
                                        verificationResult = VerificationResult.ALREADY_AUTHENTICATED_PLAYER_EXISTS;
                                    }

                                } else {
                                    verificationResult = VerificationResult.FAIL;
                                }
                            }
                            catch (Exception e) {
                                verificationResult = VerificationResult.ERROR;
                            }
                            switch (verificationResult) {
                                case SUCCESS:
                                    result = "Successfully verified reddit user " + message.getAuthor() + " with minecraft username " + offlinePlayer.getName() + " (UUID: " + uuid.toString() + ")";
                                    break;

                                case SUCCESS_NO_FLAIR:
                                    result = "Successfully verified reddit user " + message.getAuthor() + " with minecraft username " + offlinePlayer.getName() + " (UUID: " + uuid.toString() + ") -- WARNING: FLAIR UNSUCCESSFULLY APPLIED.";
                                    break;

                                case FAIL:
                                    result = "Failed to verify reddit user " + message.getAuthor() + " with minecraft username " + offlinePlayer.getName() + " (UUID: " + uuid.toString() + ")";
                                    break;

                                case ALREADY_AUTHENTICATED_PLAYER_EXISTS:
                                    result = "Failed to verify reddit user " + message.getAuthor() + " with minecraft username " + offlinePlayer.getName() + " (UUID: " + uuid.toString() + ")" + " -- already a minecraft player with this authenticated reddit account!";
                                    break;

                                case TOKEN_EXPIRED:
                                    result = "Authentication token expired for reddit user " + message.getAuthor() + " with minecraft username " + offlinePlayer.getName() + " (UUID: " + uuid.toString() + ")";
                                    break;

                                default:
                                    result = "Error occured when verifying valid verification message from reddit user " + message.getAuthor();
                                    break;
                            }
                            messageVerificationResult(offlinePlayer, verificationResult);
                        }
                        else {
                            result = "Verification message from reddit user " + message.getAuthor() + " contains valid minecraft username but does not exist.";
                        }
                    }
                    else {
                        result = "Verification message from reddit user " + message.getAuthor() + " contains invalid minecraft username.";
                    }
                }
                else {
                    result = "Skipping non-verification message from reddit user " + message.getAuthor();
                }
                Reddit.setMessageRead(message);
                Messaging.getLogger().info("VERIFICATION RESULT: " + result);
                if (sendResults) Messaging.sendPrefixedMessage(sender, result);
            }
        }
    }

    public VerificationResult verify(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        try {
            endpointEngine.updateAuthenticatedStatus(uuid, true);
            endpointEngine.removeAuthRecord(uuid);
        }
        catch (Exception e) {
            return VerificationResult.ERROR;
        }
        try {
            applyFlair(offlinePlayer, settingsConfigSection.getBoolean("flair-on-after-success"));
            return VerificationResult.SUCCESS;
        }
        catch (Exception e) {
            Error.handleException(Bukkit.getConsoleSender(), e);
            flairManager.remove(offlinePlayer);
            return VerificationResult.SUCCESS_NO_FLAIR;
        }
    }

    public void messageVerificationResult(OfflinePlayer player, VerificationResult verificationResult) {
        LocalDateTime expiry = LocalDateTime.now().plusDays(settingsConfigSection.getLong("ambient-messages-expiry-days"));
        try {
            Messaging.sendPrefixedAmbientMessage(player, messagesConfigSection.getString(verificationResult.getConfigMessageKey()), Timestamp.valueOf(expiry));
        }
        catch (SQLException e) {
            Messaging.getLogger().severe("Failed to send ambient auth verification message to player with UUID " + player.getUniqueId().toString());
            Error.handleException(Bukkit.getConsoleSender(), e);
        }
    }

    private void applyFlair(OfflinePlayer player, boolean flairOn) throws FlairException {
        FlairStatus flairStatus = flairOn ? FlairStatus.ON : FlairStatus.OFF;
        endpointEngine.updateFlair(player.getUniqueId(), flairStatus);
        flairManager.update(player);

    }
}
