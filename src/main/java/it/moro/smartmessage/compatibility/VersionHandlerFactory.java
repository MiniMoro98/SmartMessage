package it.moro.smartmessage.compatibility;

import org.bukkit.Bukkit;

public class VersionHandlerFactory {

    private static VersionHandler handler;

    public static VersionHandler getHandler() {
        if (handler == null) {
            handler = new ModernVersionHandler();
            if (handler.isSupported()) {
                return handler;
            }
            handler = new LegacyVersionHandler();
            if (handler.isSupported()) {
                return handler;
            }
            throw new UnsupportedOperationException("This version of Minecraft is not supported: " +
                    Bukkit.getServer().getVersion());
        }
        return handler;
    }
}