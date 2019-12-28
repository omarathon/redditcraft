package dev.omarathon.redditcraft.commands.admin.auth.verification;

import dev.omarathon.redditcraft.auth.verifier.Verifier;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.helper.Messaging;
import org.bukkit.command.CommandSender;

public class RunHandler extends Handler {
    private Verifier verifier;

    public RunHandler(VerificationSelector from) {
        super("run", from);
        verifier = from.getVerifier();
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length > 1) {
            warnIncorrectUsage(sender);
            return;
        }
        boolean sendResults = true;
        if (args.length == 1 && (args[0].equalsIgnoreCase("silent") || args[0].equalsIgnoreCase("quiet"))) {
            sendResults = false;
        }
        if (sendResults) Messaging.sendPrefixedMessage(sender, "Running verifier verbosely...");
        else Messaging.sendPrefixedMessage(sender, "Running verifier silently...");
        verifier.run(sender, sendResults);
    }
}
