package com.angie.attendancecheck.util;

import com.angie.attendancecheck.manager.ConfigManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    public static String get(String path) {
        String msg = ConfigManager.messagesConfig.getString(path, "메시지 없음");
        return ChatColor.translateAlternateColorCodes('&', msg.replace("{prefix}",
                ChatColor.translateAlternateColorCodes('&', ConfigManager.messagesConfig.getString("prefix", ""))));
    }

    public static List<String> getList(String path) {
        List<String> lines = ConfigManager.messagesConfig.getStringList(path);
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(ChatColor.translateAlternateColorCodes('&',
                    line.replace("{prefix}", ConfigManager.messagesConfig.getString("prefix", ""))));
        }
        return result;
    }

    public static String replace(String msg, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue());
        }
        return msg;
    }
}
