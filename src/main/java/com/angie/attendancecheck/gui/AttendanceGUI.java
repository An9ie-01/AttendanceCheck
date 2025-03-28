package com.angie.attendancecheck.gui;

import com.angie.attendancecheck.AttendanceCheck;
import com.angie.attendancecheck.manager.AttendanceManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.List;

public class AttendanceGUI {

    private static final File file = new File(AttendanceCheck.getInstance().getDataFolder(), "gui.yml");
    private static final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static void open(Player player) {
        int rows = config.getInt("gui_slot", 3);
        // 색상 코드 변환 적용
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("gui_name", "출석체크"));
        Inventory gui = Bukkit.createInventory(null, rows * 9, title);

        boolean canCheck = AttendanceManager.canCheckAttendance(player);
        String key = canCheck ? "attendance_button" : "waiting_button";

        String path = "items." + key;
        int slot = config.getInt(path + ".slot", 13);
        Material material = Material.matchMaterial(config.getString(path + ".material", "PAPER"));

        // 아이템 이름과 로어에 색상 코드 변환 적용
        String name = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".name", "출석"));
        List<String> lore = config.getStringList(path + ".lore");
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
        }

        // 플레이스홀더 치환
        String remaining = AttendanceManager.getRemainingTime(player);
        lore.replaceAll(line -> line.replace("{player}", player.getName()).replace("{date}", remaining));

        // 아이템 생성
        ItemStack item = new ItemStack(material != null ? material : Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.setCustomModelData(config.getInt(path + ".custom_model_data", 0));
        item.setItemMeta(meta);

        gui.setItem(slot, item);
        player.openInventory(gui);
    }
    public static String getTitle() {
        // 색상 코드 변환 적용
        return ChatColor.translateAlternateColorCodes('&', config.getString("gui_name", "출석체크"));
    }
}
