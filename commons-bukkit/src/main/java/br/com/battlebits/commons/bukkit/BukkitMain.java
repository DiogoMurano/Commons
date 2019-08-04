package br.com.battlebits.commons.bukkit;

import br.com.battlebits.commons.CommonPlatform;
import br.com.battlebits.commons.Commons;
import br.com.battlebits.commons.backend.DataAccount;
import br.com.battlebits.commons.backend.DataServer;
import br.com.battlebits.commons.backend.DataTeam;
import br.com.battlebits.commons.backend.logging.DataLog;
import br.com.battlebits.commons.backend.mongodb.MongoDatabase;
import br.com.battlebits.commons.backend.mongodb.MongoStorageDataAccount;
import br.com.battlebits.commons.backend.nullable.VoidDataLog;
import br.com.battlebits.commons.backend.nullable.VoidDataServer;
import br.com.battlebits.commons.backend.nullable.VoidDataTeam;
import br.com.battlebits.commons.backend.properties.PropertiesStorageDataTranslation;
import br.com.battlebits.commons.bukkit.command.BukkitCommandFramework;
import br.com.battlebits.commons.bukkit.listener.AccountListener;
import br.com.battlebits.commons.bukkit.listener.PlayerListener;
import br.com.battlebits.commons.bukkit.services.Services;
import br.com.battlebits.commons.bukkit.services.scoreboard.ScoreboardService;
import br.com.battlebits.commons.bukkit.services.scoreboard.impl.ScoreboardServiceImpl;
import br.com.battlebits.commons.bukkit.translate.BukkitTranslationCommon;
import br.com.battlebits.commons.command.CommandLoader;
import br.com.battlebits.commons.server.ServerType;
import br.com.battlebits.commons.translate.Language;
import br.com.battlebits.commons.translate.TranslateTag;
import br.com.battlebits.commons.translate.TranslationCommon;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class BukkitMain extends JavaPlugin {

    @Getter
    private static BukkitMain instance;
    @Getter
    private ProtocolManager protocolManager;


    private TranslationCommon translationCommon;

    @Override
    public void onLoad() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();


        Services.add(ScoreboardService.class, new ScoreboardServiceImpl());
    }

    @Override
    public void onEnable() {
        try {
            String serverId = "NONE"; // TODO getServerId
            ServerType type = ServerType.DEFAULT; // TODO getServerType
            // TODO Check for config file and initialize Commons
            MongoDatabase database = new MongoDatabase("localhost", "test", "test", "test", 27017);
            database.connect();

            DataServer dataServer = new VoidDataServer();
            DataAccount dataAccount = new MongoStorageDataAccount(database);
            DataTeam dataTeam = new VoidDataTeam();
            DataLog dataLog = new VoidDataLog();
            CommonPlatform platform = new BukkitPlatform();

            Commons.initialize(getLogger(), serverId, type, dataAccount, dataTeam, dataServer, dataLog, platform);
        } catch (Exception e) {
            e.printStackTrace();
        }

        translationCommon = new BukkitTranslationCommon(new PropertiesStorageDataTranslation(getFile()));
        translationCommon.onEnable();

        try {
            new CommandLoader(new BukkitCommandFramework(this)).loadCommandsFromPackage(getFile(), "br.com.battlebits.commons.bukkit.command.register");
        } catch (Exception e) {
            getLogger().severe("An internal error happened when trying to register commands!");
            e.printStackTrace();
        }

        registerListeners();
        // getServer().getScheduler().runTaskLater(this, () -> unregisterCommands("pl", "plugins", "icanhasbukkit", "ver", "version", "?", "help", "me"), 2L);
        Commons.getLogger().info("Plugin has enabled successfully");
        System.out.println(TranslationCommon.tl(Language.PORTUGUESE, TranslateTag.COMMAND_NO_PERMISSION));
    }

    @Override
    public void onDisable() {
        instance = null;
        translationCommon.onDisable();
        Commons.getLogger().info("Plugin has disabled successfully");
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new AccountListener(), this);
    }

    @SuppressWarnings("unchecked")
    private void unregisterCommands(String... commands) {
        try {
            Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f1.setAccessible(true);
            CommandMap commandMap = (CommandMap) f1.get(Bukkit.getServer());
            Field f2 = commandMap.getClass().getDeclaredField("knownCommands");
            f2.setAccessible(true);

            HashMap<String, Command> knownCommands = (HashMap<String, Command>) f2.get(commandMap);
            for (String command : commands) {
                if (knownCommands.containsKey(command)) {
                    knownCommands.remove(command);
                    List<String> aliases = new ArrayList<>();
                    for (String key : knownCommands.keySet()) {
                        if (!key.contains(":")) continue;
                        String substr = key.substring(key.indexOf(":") + 1);
                        if (substr.equalsIgnoreCase(command)) {
                            aliases.add(key);
                        }
                    }
                    for (String alias : aliases) {
                        knownCommands.remove(alias);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
