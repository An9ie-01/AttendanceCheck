package com.angie.attendancecheck.manager;

import com.angie.attendancecheck.AttendanceCheck;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RewardManager {

    private static final File file = new File(AttendanceCheck.getInstance().getDataFolder(), "attendance.yml");
    private static final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static void saveRewards(List<ItemStack> items) {
        config.set("rewards", items);
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ItemStack> getRewards() {
        List<ItemStack> result = new ArrayList<>();
        List<?> raw = config.getList("rewards");

        if (raw != null) {
            for (Object obj : raw) {
                if (obj instanceof ItemStack stack) {
                    result.add(stack);
                }
            }
        }

        return result;
    }
}
