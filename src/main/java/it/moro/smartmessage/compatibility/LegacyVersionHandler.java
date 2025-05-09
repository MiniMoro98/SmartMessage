package it.moro.smartmessage.compatibility;

import it.moro.smartmessage.utils.ReflectionUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class LegacyVersionHandler implements VersionHandler {

    @Override
    public void sendClickableMessage(Player player, String text, String action, String value) {
        try {
            Class<?> chatComponentTextClass = ReflectionUtils.getNMSClass("ChatComponentText");
            Class<?> iChatBaseComponentClass = ReflectionUtils.getNMSClass("IChatBaseComponent");
            Class<?> chatClickableClass = ReflectionUtils.getNMSClass("ChatClickable");
            Class<?> enumClickActionClass = ReflectionUtils.getNMSClass("EnumClickAction");
            Constructor<?> chatComponentConstructor = ReflectionUtils.getConstructor(chatComponentTextClass, String.class);
            Object chatComponent = chatComponentConstructor.newInstance(text);
            Object enumClickAction = null;
            if ("URL".equalsIgnoreCase(action)) {
                enumClickAction = enumClickActionClass.getField("OPEN_URL").get(null);
            } else if ("COMMAND".equalsIgnoreCase(action)) {
                enumClickAction = enumClickActionClass.getField("RUN_COMMAND").get(null);
            }
            Constructor<?> clickableConstructor = ReflectionUtils.getConstructor(chatClickableClass, enumClickActionClass, String.class);
            Object clickable = clickableConstructor.newInstance(enumClickAction, value);
            Method setChatClickable = ReflectionUtils.getMethod(iChatBaseComponentClass, "setChatClickable", chatClickableClass);
            setChatClickable.invoke(chatComponent, clickable);
            Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            Class<?> packetPlayOutChatClass = ReflectionUtils.getNMSClass("PacketPlayOutChat");
            Constructor<?> packetConstructor = ReflectionUtils.getConstructor(packetPlayOutChatClass, iChatBaseComponentClass, byte.class);
            Object packet = packetConstructor.newInstance(chatComponent, (byte) 0);
            Method sendPacket = ReflectionUtils.getMethod(playerConnection.getClass(), "sendPacket", ReflectionUtils.getNMSClass("Packet"));
            sendPacket.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSupported() {
        try {
            ReflectionUtils.getNMSClass("ChatComponentText");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}