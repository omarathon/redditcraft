package dev.omarathon.redditcraft.commands.admin.auth.handlers;

import dev.omarathon.redditcraft.auth.verifier.VerificationResult;
import dev.omarathon.redditcraft.auth.verifier.Verifier;
import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.helper.Error;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.helper.RedditHelper;
import dev.omarathon.redditcraft.reddit.Reddit;
import dev.omarathon.redditcraft.subreddit.flair.manager.lib.FlairException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class ForceHandler extends Handler {
    private Verifier verifier;

    public ForceHandler(AuthSelector from) {
        super("force", from);
        verifier = from.getAuthManager().getVerifier();
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length != 2) {
            warnIncorrectUsage(sender);
            return;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        }
        catch (IllegalArgumentException e) {
            Messaging.sendPrefixedMessage(sender, "&cProvided UUID is invalid!");
            return;
        }
        String mcUsername = Bukkit.getOfflinePlayer(uuid).getName();
        if (mcUsername == null) {
            Messaging.sendPrefixedMessage(sender, "&e&lWARNING: &ePlayer name not resolvable!");
        }
        else {
            Messaging.sendPrefixedMessage(sender, "&e&lUSERNAME: &e" + mcUsername);
        }
        String redditUsername = args[1];
        if (!RedditHelper.validUsername(redditUsername)) {
            Messaging.sendPrefixedMessage(sender, "&cInvalid reddit username!");
            return;
        }
        if (!(Reddit.userExists(redditUsername))) {
            Messaging.sendPrefixedMessage(sender, "&cReddit user with provided username doesn't exist!");
            return;
        }

        try {
            List<UUID> results = endpointEngine.getAuthenticatedUuidsWithRedditUsername(redditUsername);
            if (!results.isEmpty() && !((results.size() == 1 && results.get(0).equals(uuid)))) {
                Messaging.sendPrefixedMessage(sender, "&cFAILURE: Another player with UUID &e" + results.get(0).toString() + " &chas authenticated the given reddit account! Please void them if you wish to override.");
                return;
            }
            if (endpointEngine.existsAccountRecord(uuid)) {
                Messaging.sendPrefixedMessage(sender, "&e&lWARNING: &ePreviously had an associated reddit account with the name of &6" + endpointEngine.getRedditUsername(uuid) + " &e- overwriting!");
                endpointEngine.updateRedditUsername(uuid, redditUsername);
                endpointEngine.updateAuthenticatedStatus(uuid, false);
            }
            else {
                endpointEngine.addNewAccount(uuid, redditUsername, false);
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            VerificationResult verificationResult;
            try {
                verificationResult = verifier.verify(offlinePlayer);
            }
            catch (Exception e) {
                verificationResult = VerificationResult.ERROR;
            }
            verifier.messageVerificationResult(offlinePlayer, verificationResult);
        }
        catch (Exception e) {
            Error.handleException(sender, e);
            return;
        }
        Messaging.sendPrefixedMessage(sender, "&aSuccessfully forced authentication!");
    }
}
