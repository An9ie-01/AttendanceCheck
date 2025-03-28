package com.angie.attendancecheck.manager;

import com.angie.attendancecheck.AttendanceCheck;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    public static FileConfiguration attendanceConfig;
    public static FileConfiguration guiConfig;
    public static FileConfiguration messagesConfig;
    public static FileConfiguration logsConfig;
    public static FileConfiguration mainConfig;

    public static void reloadAll() {
        AttendanceCheck plugin = AttendanceCheck.getInstance();
        mainConfig = plugin.getConfig();

        attendanceConfig = loadConfig(plugin, "attendance.yml");
        guiConfig = loadConfig(plugin, "gui.yml");
        messagesConfig = loadConfig(plugin, "messages.yml");
        logsConfig = loadConfig(plugin, "logs.yml");
    }

    private static FileConfiguration loadConfig(AttendanceCheck plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void init() {
        reloadAll();
    }
}

