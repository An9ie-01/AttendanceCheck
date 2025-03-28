package com.angie.attendancecheck.command;

import com.angie.attendancecheck.gui.AttendanceGUI; // ✅ import 되어 있어야 함
import com.angie.attendancecheck.manager.AttendanceManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AttendanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        AttendanceGUI.open(player); // ✅ GUI 열기

        return true;
    }
}
