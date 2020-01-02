package dev.omarathon.redditcraft;

import dev.omarathon.ambientmessenger.AmbientMessenger;
import dev.omarathon.redditapi.RedditAPI;
import dev.omarathon.redditapi.connect.InitialisingConnectHandler;
import dev.omarathon.redditcraft.auth.AuthManager;
import dev.omarathon.redditcraft.commands.MainCommandExecutor;
import dev.omarathon.redditcraft.commands.RedditCommandExecutor;
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
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class RedditCraft extends JavaPlugin {
    private static RedditCraft instance;

    private AuthManager authManager;
    private SubredditManager subredditManager;
    private EndpointEngine endpointEngine;

    private static Permission perms = null;
    private static Chat chat = null;
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

        getConfig().options().copyDefaults(true);
        saveConfig();
        Config.loadConfig();

        Reddit.setLogging(Config.getConfig().getBoolean("verbose"));

        // setup SQL
        try {
            sql = new SQL(Config.getSQLConfiguation());
            getLogger().info("Successfully connected MySQL!");
        }
        catch (SQLException e) {
            getLogger().severe("[FATAL] Error in connecting MySQL!");
            e.printStackTrace();
            setEnabled(false);
            return;
        }

        // load vault
        if (!loadVault()) {
            getLogger().severe("[FATAL] Error in setting up Vault!");
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
            subredditManager = new SubredditManager(endpointEngine, chat, perms);
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
        // bind reddit command executor
        getCommand("reddit").setExecutor(new RedditCommandExecutor(endpointEngine));

        getLogger().info("[SUCCESS] Successfully started RedditCraft!");
    }

    private boolean loadVault() {
        if (!setupPermissions()) return false;
        if (!setupChat()) return false;
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
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
        getLogger().warning("Stopping RedditCraft!");

        // disable AmbientMessenger
        try {
            Messaging.getAmbientMessenger().disable();
        }
        catch (SQLException e) {
            getLogger().severe("[ERROR] Failed to disable AmbientMessenger!");
        }
        // stop auth manager scheduled services
        if (authManager != null) authManager.stopScheduledServices();
        // close sql connection
        try {
            sql.close();
        }
        catch (SQLException e) {
            getLogger().severe("[ERROR] Failed to close SQLConnection!");
        }

        // done
        getLogger().warning("Stopped RedditCraft!");
    }

    public static RedditCraft getInstance() {
        return instance;
    }

    public EndpointEngine getEndpointEngine() {
        return endpointEngine;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }
}
