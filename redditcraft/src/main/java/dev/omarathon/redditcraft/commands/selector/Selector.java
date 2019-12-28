package dev.omarathon.redditcraft.commands.selector;

import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.handler.Handler;
import dev.omarathon.redditcraft.commands.handler.presets.HelpHandler;
import dev.omarathon.redditcraft.commands.lib.Command;
import dev.omarathon.redditcraft.commands.lib.PermissionResult;
import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.subreddit.flair.manager.FlairManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Selector extends Command {
    private Map<String, Command> commandMap;
    private EndpointEngine endpointEngine;
    private AuthManager authManager;
    private FlairManager flairManager;

    public Selector(String formalArg, Selector from) {
        super(formalArg, from);
        commandMap = new HashMap<>();
        endpointEngine = from.getEndpointEngine();
        authManager = from.getAuthManager();
        flairManager = from.getFlairManager();
        bind(new HelpHandler(this));
    }

    // base selector
    public Selector(String formalArg, EndpointEngine endpointEngine, AuthManager authManager, FlairManager flairManager) {
        super(formalArg, null);
        commandMap = new HashMap<>();
        this.endpointEngine = endpointEngine;
        this.authManager = authManager;
        this.flairManager = flairManager;
        bind(new HelpHandler(this));
    }

    public void bind(Command command) {
        for (String arg : command.getArgs()) {
            commandMap.put(arg, command);
        }
    }

    public void bindMultiple(Iterable<Command> commands) {
        for (Command command: commands) {
            bind(command);
        }
    }

    public void execute(SelectorArgs args) {
        // unpack SelectorArgs
        CommandSender sender = args.getSender();
        List<String> commandArgs = args.getArgs();

        Command command = commandMap.get(args.getArg());

        // if command doesn't exist from this selector, not found, send to help
        if (command == null) {
            command = commandMap.get("help");
        }

        // query permission if not wildcarded
        if (!args.isWildcarded()) {
            PermissionResult permissionResult = command.hasPermission(sender);
            switch (permissionResult) {
                case WILDCARDED:
                    args.setWildcarded(true);

                case ALLOWED:
                    break;

                default:
                    command.warnNoPermission(sender);
                    return;
            }
        }

        if (command instanceof Handler) {
            Handler handler = (Handler) command;
            String[] handlerArgs; // args to the handler
            try {
                // move to next arg, will throw IllegalStateException if no more args left
                args.incrementArgIndex();
                // handlerArgs contains the remaining args
                int argIndex = args.getArgIndex();
                int argsSize = commandArgs.size();
                handlerArgs = new String[argsSize - argIndex];
                for (int i = argIndex; i < argsSize; i++) {
                    handlerArgs[i - argIndex] = commandArgs.get(i);
                }
            }
            catch (IllegalStateException e) { // last arg doesn't lead to a handler
                // no more args, so no args to the command
                handlerArgs = new String[0];
            }
            handler.handle(args.getSender(), handlerArgs);
        }
        else if (command instanceof Selector) {
            try {
                args.incrementArgIndex();
            }
            catch (IllegalStateException e) { // last arg doesn't lead to a handler, go to help
                commandArgs.add("help");
                execute(args);
                return;
            }
            ((Selector) command).execute(args);
        }
    }

    public EndpointEngine getEndpointEngine() {
        return endpointEngine;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public FlairManager getFlairManager() {
        return flairManager;
    }
}
