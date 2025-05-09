package it.moro.smartmessage.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    private static String version = null;

    /**
     * Ottiene la versione del server NMS
     * @return La stringa della versione (es. v1_16_R3)
     */
    public static String getVersion() {
        if (version == null) {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            version = packageName.substring(packageName.lastIndexOf('.') + 1);
        }
        return version;
    }

    /**
     * Ottiene una classe NMS
     * @param className Il nome della classe
     * @return La classe
     */
    public static Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + className);
        } catch (ClassNotFoundException e) {
            try {
                // Per versioni 1.17+
                return Class.forName("net.minecraft." + className);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Ottiene una classe CraftBukkit
     * @param className Il nome della classe
     * @return La classe
     */
    public static Class<?> getCraftBukkitClass(String className) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ottiene un metodo da una classe
     * @param clazz La classe
     * @param methodName Il nome del metodo
     * @param parameterTypes I tipi di parametri
     * @return Il metodo
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ottiene un costruttore da una classe
     * @param clazz La classe
     * @param parameterTypes I tipi di parametri
     * @return Il costruttore
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}