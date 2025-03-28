package com.angie.attendancecheck;

import com.angie.attendancecheck.command.AttendanceCommand;
import com.angie.attendancecheck.command.AdminCommand;
import com.angie.attendancecheck.listener.GUIListener;
import com.angie.attendancecheck.manager.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class AttendanceCheck extends JavaPlugin {

    private static AttendanceCheck instance;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.init();
// onEnable() 또는 ConfigManager.init() 내부
        File file = new File(getDataFolder(), "attendance.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("type")) {
            config.set("type", "normal");
            try {
                config.save(file);
            } catch (IOException e) {
                getLogger().severe("attendance.yml 저장 중 오류 발생!");
                e.printStackTrace();
            }
        }

        saveDefaultConfig();
        saveResourceIfNotExists("attendance.yml");
        saveResourceIfNotExists("messages.yml");
        saveResourceIfNotExists("gui.yml");
        saveResourceIfNotExists("logs.yml");
        saveResourceIfNotExists("config.yml");

        // 명령어 등록
        getCommand("attendance").setExecutor(new AttendanceCommand());
        getCommand("attendanceadmin").setExecutor(new AdminCommand());

        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getLogger().info("출석체크 플러그인이 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        getLogger().info("출석체크 플러그인이 비활성화되었습니다.");
    }

    public static AttendanceCheck getInstance() {
        return instance;
    }

    private void saveResourceIfNotExists(String name) {
        File file = new File(getDataFolder(), name);
        if (!file.exists()) {
            try {
                saveResource(name, false);
            } catch (IllegalArgumentException e) {
                // 해당 리소스가 JAR 내부에 없으면, 빈 파일로 생성
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    getLogger().warning(name + " 리소스를 찾을 수 없어 빈 파일을 생성했습니다.");
                } catch (Exception ex) {
                    getLogger().severe(name + " 파일 생성에 실패했습니다.");
                    ex.printStackTrace();
                }
            }
        }
    }
}
