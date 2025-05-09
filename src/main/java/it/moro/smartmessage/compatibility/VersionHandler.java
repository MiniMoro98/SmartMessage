package it.moro.smartmessage.compatibility;

import org.bukkit.entity.Player;

public interface VersionHandler {
    /**
     * Invia un messaggio cliccabile al giocatore
     * @param player Il giocatore
     * @param text Il testo da mostrare
     * @param action Il tipo di azione (URL, COMMAND, ecc.)
     * @param value Il valore dell'azione (URL o comando)
     */
    void sendClickableMessage(Player player, String text, String action, String value);

    /**
     * Verifica se questa versione è supportata
     * @return true se la versione è supportata
     */
    boolean isSupported();
}