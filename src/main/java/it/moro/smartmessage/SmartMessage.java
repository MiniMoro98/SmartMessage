package it.moro.smartmessage;

import it.moro.smartmessage.compatibility.VersionHandler;
import it.moro.smartmessage.compatibility.VersionHandlerFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SmartMessage extends JavaPlugin {

    private File Configuration;
    private FileConfiguration config;
    private int interval;
    private int currentIndex = 0;
    private final List<String> entry = new ArrayList<>();
    private final List<String> texts = new ArrayList<>();
    private final List<String> links = new ArrayList<>();
    private final List<String> commands = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("SmartMessage initialization for server version: " + Bukkit.getServer().getVersion());

        try {
            VersionHandlerFactory.getHandler();
            getLogger().info("Version compatibility detected successfully!");
        } catch (UnsupportedOperationException e) {
            getLogger().severe("ERROR: " + e.getMessage());
            getLogger().severe("The plugin will be disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        genConfig();
        loadConfig();
        startMessage();
        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled!");
    }

    private void genConfig() {
        Configuration = new File(getDataFolder(), "config.yml");
        if (!Configuration.exists()) {
            Configuration.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(Configuration);
    }

    private void loadConfig() {
        interval = config.getInt("message-interval", 60);

        for (String key : Objects.requireNonNull(config.getConfigurationSection("message")).getKeys(false)) {
            entry.add(key);
            texts.add(Objects.requireNonNull(config.getString("message." + key + ".text")).replaceAll("&", "§"));
            links.add(config.getString("message." + key + ".link"));
            commands.add(config.getString("message." + key + ".command"));
        }
        if (texts.isEmpty()) {
            texts.add("Error!");
            links.add("");
            commands.add("");
            saveConfigFile();
        }
    }

    private void saveConfigFile() {
        try {
            config.save(Configuration);
        } catch (IOException e) {
            getLogger().severe("Unable to save configuration file!");
            e.fillInStackTrace();
        }
    }

    private void startMessage() {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendMessage(currentIndex);
                currentIndex = (currentIndex + 1) % texts.size();
            }
        }.runTaskTimer(this, 0, interval * 20L);
    }

    private void sendMessage(int index) {
        String text = texts.get(index);
        String link = links.get(index);
        String cmd = commands.get(index);
        String prefix = Objects.requireNonNull(config.getString("prefix")).replaceAll("&","§");
        String formattedText = text.replaceAll("%prefix%", prefix);

        VersionHandler versionHandler = VersionHandlerFactory.getHandler();

        if (link.isEmpty() && cmd.isEmpty() && !text.isEmpty()) {
            // Messaggio semplice
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("smartmessage.message")) {
                    versionHandler.sendClickableMessage(player, formattedText, null, null);
                }
            }
        } else if (!link.isEmpty() && cmd.isEmpty() && !text.isEmpty()) {
            // Messaggio con link
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("smartmessage.link")) {
                    versionHandler.sendClickableMessage(player, formattedText, "URL", link);
                }
            }
        } else if (link.isEmpty() && !cmd.isEmpty() && !text.isEmpty()) {
            // Messaggio con comando
            if(!cmd.contains("/")) {
                cmd = "/" + cmd;
            }
            final String finalCmd = cmd;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("smartmessage.cmd")) {
                    versionHandler.sendClickableMessage(player, formattedText, "COMMAND", finalCmd);
                }
            }
        } else if (!link.isEmpty() && !cmd.isEmpty() && !text.isEmpty()) {
            // Errore: entrambi link e comando specificati
            String errorMessage = "§e[SmartMessage] §cMessage '" + entry.get(index) +
                    "' §cError! Insert only a link or only a command, both functions are not supported.";
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("smartmessage.message")) {
                    versionHandler.sendClickableMessage(player, errorMessage, null, null);
                }
            }
        }
    }
}
