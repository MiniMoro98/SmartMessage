package it.moro.smartmessage.compatibility;

import org.bukkit.entity.Player;

public interface VersionHandler {
    void sendClickableMessage(Player player, String text, String action, String value);
    boolean isSupported();
}