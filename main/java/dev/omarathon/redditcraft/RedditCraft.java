package dev.omarathon.redditcraft;

import dev.omarathon.ambientmessenger.AmbientMessenger;
import dev.omarathon.redditapi.RedditAPI;
import dev.omarathon.redditapi.connect.InitialisingConnectHandler;
import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.MainCommandExecutor;
import dev.omarathon.redditcraft.data.EndpointEngine;
import dev.omarathon.redditcraft.data.engines.presets.sql.SQLAccountTableDataEngine;
import dev.omarathon.redditcraft.data.engines.presets.sql.SQLAuthTableDataEngine;
import dev.omarathon.redditcraft.data.engines.presets.sql.connection.SQL;
import dev.omarathon.redditcraft.helper.Config;
import dev.omarathon.redditcraft.helper.Messaging;
import dev.omarathon.redditcraft.papi.RedditCraftExpansion;
import dev.omarathon.redditcraft.reddit.Reddit;
import dev.omarathon.redditcraft.subreddit.SubredditManager;
import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class RedditCraft extends JavaPlugin {
    private static RedditCraft instance;

    private AuthManager authManager;
    private SubredditManager subredditManager;
    private EndpointEngine endpointEngine;

    private SQL sql;

    public RedditCraft() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("Binding RedditAPI ConnectHandler...");
        RedditAPI.registerConnectHandler(new InitialisingConnectHandler() {
            @Override
            public void onFirstConnect(RedditClient redditClient) {
                onNonFirstConnect(redditClient);
                load();
                // bind papi expansion if it is installed
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    Messaging.getLogger().info("PAPI found, registering expansion...");
                    new RedditCraftExpansion(RedditCraft.this).register();
                    Messaging.getLogger().info("Successfully registered PAPI expansion!");
                }
                else {
                    Messaging.getLogger().info("PAPI not found, expansion will not be registered.");
                }
            }
            @Override
            public void onNonFirstConnect(RedditClient redditClient) {
                Messaging.getLogger().info("Updating RedditClient...");
                Reddit.setRedditClient(redditClient);
            }
        });
        getLogger().info("Successfully bound RedditAPI ConnectHandler, awaiting RedditAPI to connect...");
    }

    public void load() {
        getLogger().info("Loading RedditCraft...");

        saveDefaultConfig();

        Config.loadConfig();

        // setup SQL
        try {
            sql = new SQL(Config.getSQLConfiguation());
            getLogger().info("Successfully connected MySQL!");
        }
        catch (SQLException | ClassNotFoundException e) {
            getLogger().severe("[FATAL] Error in connecting MySQL!");
            e.printStackTrace();
            setEnabled(false);
            return;
        }

        this.endpointEngine = new EndpointEngine(new SQLAccountTableDataEngine(sql), new SQLAuthTableDataEngine(sql));

        setupDB();

        try {
            setupAmbientMessenger();
            getLogger().info("Successfully setup AmbientMessenger!");
        }
        catch (SQLException e) {
            getLogger().severe("[FATAL] Error in setting up AmbientMessenger!");
            e.printStackTrace();
            setEnabled(false);
            return;
        }

        try {
            subredditManager = new SubredditManager(endpointEngine);
        }
        catch (IllegalArgumentException | IllegalStateException | ApiException e) {
            getLogger().severe("[FATAL] Error in setting up SubredditManager!");
            e.printStackTrace();
            setEnabled(false);
            return;
        }

        authManager = new AuthManager(subredditManager.getFlairManager(), endpointEngine);
        authManager.runScheduledServices();

        // bind main command executor
        getCommand("redditcraft").setExecutor(new MainCommandExecutor(authManager, subredditManager.getFlairManager(), endpointEngine));

        getLogger().info("[SUCCESS] Successfully started RedditCraft!");
    }

    private void setupDB() {
        if (!endpointEngine.accountTableExists()) {
            endpointEngine.createAccountTable();
        }
        if (!endpointEngine.authTableExists()) {
            endpointEngine.createAuthTable();
        }
    }

    private void setupAmbientMessenger() throws SQLException {
        AmbientMessenger ambientMessenger = new AmbientMessenger(sql.getConnection(), true);
        getServer().getPluginManager().registerEvents(ambientMessenger, this);
        Messaging.setAmbientMessenger(ambientMessenger);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (authManager != null) authManager.stopScheduledServices();
        getLogger().warning("Stopping RedditCraft!");
    }

    public static RedditCraft getInstance() {
        return instance;
    }

    public EndpointEngine getEndpointEngine() {
        return endpointEngine;
    }
}
