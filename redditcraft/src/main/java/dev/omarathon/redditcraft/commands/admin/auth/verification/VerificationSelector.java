package dev.omarathon.redditcraft.commands.admin.auth.verification;

import dev.omarathon.redditcraft.auth.verifier.Verifier;
import dev.omarathon.redditcraft.commands.admin.auth.AuthSelector;
import dev.omarathon.redditcraft.commands.selector.Selector;

public class VerificationSelector extends Selector {
    private Verifier verifier;

    public VerificationSelector(AuthSelector from) {
        super("verification", from);
        verifier = from.getAuthManager().getVerifier();
        addArg("verifier");
        addArg("verify");
        bind(new RunHandler(this));
    }

    public Verifier getVerifier() {
        return verifier;
    }
}
