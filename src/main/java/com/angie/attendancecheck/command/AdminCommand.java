package com.angie.attendancecheck.command;

import com.angie.attendancecheck.manager.AttendanceManager;
import com.angie.attendancecheck.manager.ConfigManager;
import com.angie.attendancecheck.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AdminCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        // 아무 인자 없이 입력하면 모든 하위 명령어 목록 출력
        if (args.length == 0) {
            player.sendMessage(ChatColor.GREEN + "/attendanceadmin reward - 출석 보상 설정 GUI 열기");
            player.sendMessage(ChatColor.GREEN + "/attendanceadmin reload - 설정 파일 리로드");
            player.sendMessage(ChatColor.GREEN + "/attendanceadmin clear <target> - 특정 플레이어 출석 기록 초기화");
            player.sendMessage(ChatColor.GREEN + "/attendanceadmin date <daily/weekly/monthly> - 출석 주기 변경");
            player.sendMessage(ChatColor.GREEN + "/attendanceadmin info <target> - 플레이어 출석 정보 조회");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reward")) {
            // 예: /attendanceadmin reward 명령어 처리
            com.angie.attendancecheck.gui.RewardGUI.open(player);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            ConfigManager.reloadAll();
            player.sendMessage(MessageUtil.get("prefix") + "설정 파일을 리로드했습니다.");
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("clear")) {
            String targetName = args[1];
            boolean result = AttendanceManager.clearLog(targetName);
            if (result) {
                String msg = MessageUtil.get("admin.cleared").replace("{target}", targetName);
                player.sendMessage(msg);
            } else {
                String msg = MessageUtil.get("admin.not-found").replace("{target}", targetName);
                player.sendMessage(msg);
            }
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("date")) {
            String mode = args[1].toLowerCase();
            if (!List.of("daily", "weekly", "monthly").contains(mode)) {
                player.sendMessage(MessageUtil.get("admin.set-date-fail"));
                return true;
            }
            File file = new File(com.angie.attendancecheck.AttendanceCheck.getInstance().getDataFolder(), "attendance.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("date", mode);
            try {
                config.save(file);
                String msg = MessageUtil.get("admin.set-date").replace("{mode}", mode);
                player.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("info")) {
            String targetName = args[1];
            File file = new File(com.angie.attendancecheck.AttendanceCheck.getInstance().getDataFolder(), "logs.yml");
            YamlConfiguration logs = YamlConfiguration.loadConfiguration(file);

            for (String uuidStr : logs.getKeys(false)) {
                OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr));
                if (offline.getName() != null && offline.getName().equalsIgnoreCase(targetName)) {
                    String lastDate = logs.getString(uuidStr);
                    String mode = com.angie.attendancecheck.AttendanceCheck.getInstance().getConfig().getString("date", "daily");

                    List<String> info = MessageUtil.getList("admin.info");
                    for (String line : info) {
                        player.sendMessage(line
                                .replace("{name}", targetName)
                                .replace("{date}", lastDate)
                                .replace("{mode}", mode));
                    }
                    return true;
                }
            }
            player.sendMessage(MessageUtil.get("admin.not-found").replace("{target}", targetName));
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // 기본 하위 명령어 목록
        List<String> subCommands = Arrays.asList("reward", "reload", "clear", "date", "info");
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String sub : subCommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("date")) {
                List<String> dateOptions = Arrays.asList("daily", "weekly", "monthly");
                for (String option : dateOptions) {
                    if (option.startsWith(args[1].toLowerCase())) {
                        completions.add(option);
                    }
                }
                return completions;
            } else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("info")) {
                // 온라인 플레이어 이름으로 탭 완성
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(online.getName());
                    }
                }
                return completions;
            }
        }
        return completions;
    }
}
