package it.moro.smartmessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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
            texts.add("Errore!");
            links.add("");
            commands.add("");
            saveConfigFile();
        }
    }

    private void saveConfigFile() {
        try {
            config.save(Configuration);
        } catch (IOException e) {
            getLogger().severe("Impossibile salvare il file di configurazione!");
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

        if (link.isEmpty() && cmd.isEmpty() && !text.isEmpty()) {
            Component mainMessage = Component.text(text.replaceAll("%prefix%", prefix));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("smartmessage.message")) {
                    player.sendMessage(mainMessage);
                }
            }

        } else if (!link.isEmpty() && cmd.isEmpty() && !text.isEmpty()) {
            Component messaggioLink = Component.text(text.replaceAll("%prefix%", prefix)).clickEvent(ClickEvent.openUrl(link));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("smartmessage.link")) {
                    player.sendMessage(messaggioLink);
                }
            }

        } else if (link.isEmpty() && !cmd.isEmpty() && !text.isEmpty()) {
            Component messaggioComando = Component.text(text.replaceAll("%prefix%", prefix)).clickEvent(ClickEvent.runCommand("/" + cmd));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("smartmessage.cmd")) {
                    player.sendMessage(messaggioComando);
                }
            }

        } else if (!link.isEmpty() && !cmd.isEmpty() && !text.isEmpty()) {
            Component messaggioComando = Component.text("§e[SmartMessage] §cMessage '" + entry.get(index)
                    + "' §cError! Insert only a link or only a command, both functions are not supported.");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.isOp()) {
                    player.sendMessage(messaggioComando);
                }
            }

        } else if (link.isEmpty() && cmd.isEmpty()) {
            Component messaggioComando = Component.text("§e[SmartMessage] §cMessage '" + entry.get(index)
                    + "' §cError! All entries are empty.");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(player.isOp()) {
                    player.sendMessage(messaggioComando);
                }
            }
        }
    }
}
