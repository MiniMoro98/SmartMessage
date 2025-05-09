package it.moro.smartmessage.compatibility;

import org.bukkit.Bukkit;

public class VersionHandlerFactory {

    private static VersionHandler handler;

    /**
     * Ottiene l'handler appropriato per la versione corrente
     * @return L'handler per la versione
     */
    public static VersionHandler getHandler() {
        if (handler == null) {
            // Prima prova con l'handler moderno (più comune)
            handler = new ModernVersionHandler();
            if (handler.isSupported()) {
                return handler;
            }

            // Se non è supportato, prova con l'handler legacy
            handler = new LegacyVersionHandler();
            if (handler.isSupported()) {
                return handler;
            }

            // Se nessun handler è supportato, lancia un'eccezione
            throw new UnsupportedOperationException("This version of Minecraft is not supported: " +
                    Bukkit.getServer().getVersion());
        }

        return handler;
    }
}