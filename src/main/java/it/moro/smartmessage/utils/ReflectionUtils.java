package it.moro.smartmessage.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    private static String version = null;

    public static String getVersion() {
        if (version == null) {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            version = packageName.substring(packageName.lastIndexOf('.') + 1);
        }
        return version;
    }

    public static Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("net.minecraft." + className);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    public static Class<?> getCraftBukkitClass(String className) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}