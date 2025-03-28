package com.angie.attendancecheck.manager;

import com.angie.attendancecheck.AttendanceCheck;
import com.angie.attendancecheck.util.MessageUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AttendanceManager {

    private static final File logFile = new File(AttendanceCheck.getInstance().getDataFolder(), "logs.yml");
    private static final YamlConfiguration logs = YamlConfiguration.loadConfiguration(logFile);

    public static boolean hasCheckedToday(Player player) {
        String uuid = player.getUniqueId().toString();
        String date = LocalDate.now().toString();

        return logs.contains(uuid) && date.equals(logs.getString(uuid));
    }

    public static void giveRewards(Player player) {
        List<ItemStack> rewards = RewardManager.getRewards();
        int freeSlots = 0;
        ItemStack[] storageContents = player.getInventory().getStorageContents();
        for (ItemStack item : storageContents) {
            if (item == null || item.getType() == Material.AIR) {
                freeSlots++;
            }
        }

        // 필요한 빈 슬롯: 보상 아이템 수 + 1
        int requiredSlots = rewards.size() + 1;

        if (freeSlots < requiredSlots) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c인벤토리에 최소 " + requiredSlots + "칸의 빈 슬롯이 필요합니다. 현재 빈 슬롯: " + freeSlots));
            return;
        }
        for (ItemStack item : rewards) {
            if (item != null) {
                player.getInventory().addItem(item.clone());
            }
        }


        player.sendMessage(MessageUtil.get("attendance.success"));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5);

        saveLog(player);
    }


    public static boolean canCheckAttendance(Player player) {
        String uuid = player.getUniqueId().toString();
        String lastDateStr = logs.getString(uuid);
        if (lastDateStr == null) return true;

        LocalDate lastDate = LocalDate.parse(lastDateStr);
        LocalDate now = LocalDate.now();

        String mode = AttendanceCheck.getInstance().getConfig().getString("date", "daily");

        long daysBetween = ChronoUnit.DAYS.between(lastDate, now);

        switch (mode.toLowerCase()) {
            case "daily":
                return daysBetween >= 1;
            case "weekly":
                return daysBetween >= 7;
            case "monthly":
                return daysBetween >= 28;
            default:
                return true;
        }
    }

    public static boolean setDateMode(String mode) {
        File file = new File(AttendanceCheck.getInstance().getDataFolder(), "attendance.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<String> valid = Arrays.asList("daily", "weekly", "monthly");
        if (!valid.contains(mode.toLowerCase())) return false;

        config.set("date", mode.toLowerCase());
        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getRemainingTime(Player player) {
        String uuid = player.getUniqueId().toString();
        String lastDateStr = logs.getString(uuid);
        if (lastDateStr == null) return "출석 가능";

        // 마지막 출석 시간 → 00:00 기준 처리
        LocalDateTime lastDateTime = LocalDate.parse(lastDateStr).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        String mode = AttendanceCheck.getInstance().getConfig().getString("date", "daily").toLowerCase();
        long coolDownDays = switch (mode) {
            case "daily" -> 1;
            case "weekly" -> 7;
            case "monthly" -> 28;
            default -> 1;
        };

        LocalDateTime nextAvailable = lastDateTime.plusDays(coolDownDays);
        if (now.isAfter(nextAvailable)) return "출석 가능";

        Duration remaining = Duration.between(now, nextAvailable);
        long days = remaining.toDays();
        long hours = remaining.toHours() % 24;
        long minutes = remaining.toMinutes() % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) result.append(days).append("일 ");
        if (hours > 0) result.append(hours).append("시간 ");
        if (minutes > 0) result.append(minutes).append("분");
        if (result.isEmpty()) result.append("잠시 후 가능");

        return result.toString().trim();
    }

    public static String getInfoByName(String name) {
        for (String uuidStr : logs.getKeys(false)) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr));
            if (offline.getName() != null && offline.getName().equalsIgnoreCase(name)) {
                String lastDate = logs.getString(uuidStr);
                Player fakePlayer = Bukkit.getPlayerExact(name); // 임시 Player 객체용

                String timeLeft = "출석 가능";
                if (fakePlayer != null) {
                    timeLeft = getRemainingTime(fakePlayer);
                }

                return "§e" + name + "§f님의 마지막 출석: §b" + lastDate + "§f / 남은 시간: §c" + timeLeft;
            }
        }
        return "§c" + name + "님의 출석 기록이 없습니다.";
    }

    public static String getStats(String name) {
        File logFile = new File(AttendanceCheck.getInstance().getDataFolder(), "logs.yml");
        YamlConfiguration logs = YamlConfiguration.loadConfiguration(logFile);

        int total = logs.getKeys(false).size(); // 전체 저장된 플레이어 수 (참고용)

        for (String uuidStr : logs.getKeys(false)) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr));
            if (offline.getName() != null && offline.getName().equalsIgnoreCase(name)) {
                String lastDate = logs.getString(uuidStr);
                String mode = AttendanceCheck.getInstance().getConfig().getString("date", "daily");
                return "§e" + name + "§f님의 출석 정보\n" +
                        "§7- 마지막 출석일: §b" + lastDate + "\n" +
                        "§7- 출석 주기: §a" + mode;
            }
        }
        return "§c" + name + "님의 출석 기록이 없습니다.";
    }


    public static void saveLog(Player player) {
        if (!AttendanceCheck.getInstance().getConfig().getBoolean("save", true)) {
            return; // 저장 비활성화 시 기록 안 함
        }

        String uuid = player.getUniqueId().toString();
        String date = LocalDate.now().toString();
        logs.set(uuid, date);
        try {
            logs.save(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean clearLog(String name) {
        for (String key : logs.getKeys(false)) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(key));
            if (offline.getName() != null && offline.getName().equalsIgnoreCase(name)) {
                logs.set(key, null);
                try {
                    logs.save(logFile);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
}
