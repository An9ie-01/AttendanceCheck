package com.angie.attendancecheck.listener;

import com.angie.attendancecheck.gui.AttendanceGUI;
import com.angie.attendancecheck.gui.RewardGUI;
import com.angie.attendancecheck.manager.AttendanceManager;
import com.angie.attendancecheck.manager.RewardManager;
import com.angie.attendancecheck.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // 색상 코드를 제거한 제목으로 비교
        if (ChatColor.stripColor(event.getView().getTitle())
                .equalsIgnoreCase(ChatColor.stripColor(AttendanceGUI.getTitle()))) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String name = clicked.getItemMeta().getDisplayName();
            if (name == null) return;

            if (name.contains("출석하기")) {
                if (AttendanceManager.canCheckAttendance(player)) {
                    AttendanceManager.giveRewards(player);
                    player.closeInventory();
                } else {
                    String msg = MessageUtil.get("attendance.not-available")
                            .replace("{time}", AttendanceManager.getRemainingTime(player));
                    player.sendMessage(msg);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (!event.getView().getTitle().equals(RewardGUI.getTitle())) return;

        List<ItemStack> contents = Arrays.asList(inv.getContents());
        RewardManager.saveRewards(contents);
    }
}
