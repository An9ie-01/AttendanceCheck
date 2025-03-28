package com.angie.attendancecheck.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RewardGUI {

    private static final String GUI_TITLE = "출석 보상 설정";

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, GUI_TITLE);

        // 저장된 보상이 있다면 불러오기
        for (ItemStack item : com.angie.attendancecheck.manager.RewardManager.getRewards()) {
            if (item != null) gui.addItem(item);
        }

        player.openInventory(gui);
    }

    public static String getTitle() {
        return GUI_TITLE;
    }
}
