package it.moro.smartmessage.compatibility;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ModernVersionHandler implements VersionHandler {

    @Override
    public void sendClickableMessage(Player player, String text, String action, String value) {
        TextComponent component = new TextComponent(text);
        if ("URL".equalsIgnoreCase(action)) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, value));
        } else if ("COMMAND".equalsIgnoreCase(action)) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, value));
        }
        player.spigot().sendMessage(component);
    }

    @Override
    public boolean isSupported() {
        try {
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}