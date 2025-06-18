package com.morreudev.discord.reports.managers;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.models.Report;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportManager {

    private final DiscordReports plugin;
    private final List<Report> reports;
    private final Map<UUID, Report> reportsMap;
    private final Map<String, Long> cooldowns;
    private final File reportsFile;
    private FileConfiguration reportsConfig;

    public ReportManager(DiscordReports plugin) {
        this.plugin = plugin;
        this.reports = new ArrayList<>();
        this.reportsMap = new ConcurrentHashMap<>();
        this.cooldowns = new ConcurrentHashMap<>();
        this.reportsFile = new File(plugin.getDataFolder(), "reports.yml");
        loadReports();
    }

    public void addReport(Report report) {
        reports.add(report);
        reportsMap.put(report.getId(), report);
        

        if (plugin.getConfigManager().getConfig().getBoolean("settings.notify-staff")) {
            String message = plugin.getConfigManager().getMessage("system.report-notification")
                .replace("{reporter}", report.getReporter())
                .replace("{reported}", report.getReported());
            
            plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("discordreports.view"))
                .forEach(p -> {
                    p.sendMessage(message);
                    String sound = plugin.getConfigManager().getConfig().getString("settings.notification-sound", "LEVEL_UP");
                    if (!sound.equals("NONE")) {
                        try {
                            p.playSound(p.getLocation(), Sound.valueOf(sound), 1.0f, 1.0f);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Som inválido configurado: " + sound);
                        }
                    }
                });
        }

        saveReports();
    }

    public void createReport(Player reporter, String reported, String reason) {

        long cooldownTime = plugin.getConfigManager().getConfig().getLong("settings.report-cooldown", 300) * 1000;
        long lastReport = cooldowns.getOrDefault(reporter.getName(), 0L);
        long timeLeft = (lastReport + cooldownTime - System.currentTimeMillis()) / 1000;

        if (timeLeft > 0) {
            reporter.sendMessage(plugin.getConfigManager().getMessage("plugin.report-cooldown")
                    .replace("{time}", String.valueOf(timeLeft)));
            return;
        }


        Report report = new Report(reporter.getName(), reported, reason);
        addReport(report);


        cooldowns.put(reporter.getName(), System.currentTimeMillis());
    }

    public void removeReport(UUID id) {
        Report report = reportsMap.get(id);
        if (report != null) {
            reports.remove(report);
            reportsMap.remove(id);
            saveReports();
        }
    }

    public void clearReports() {
        reports.clear();
        reportsMap.clear();
        saveReports();
    }

    public Collection<Report> getReports() {
        return new ArrayList<>(reports);
    }

    private void loadReports() {
        if (!reportsFile.exists()) {
            return;
        }

        YamlConfiguration reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
        
        if (reportsConfig.contains("reports")) {
            for (String key : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
                String path = "reports." + key;
                String reporter = reportsConfig.getString(path + ".reporter");
                String reported = reportsConfig.getString(path + ".reported");
                String reason = reportsConfig.getString(path + ".reason");
                Date date = new Date(reportsConfig.getLong(path + ".date"));
                boolean resolved = reportsConfig.getBoolean(path + ".resolved");
                
                Report report = new Report(reporter, reported, reason, date);
                report.setResolved(resolved);
                reports.add(report);
                reportsMap.put(report.getId(), report);
            }
        }
    }

    public void saveReports() {
        YamlConfiguration reportsConfig = new YamlConfiguration();
        
        for (Report report : reports) {
            String path = "reports." + report.getId().toString();
            reportsConfig.set(path + ".reporter", report.getReporter());
            reportsConfig.set(path + ".reported", report.getReported());
            reportsConfig.set(path + ".reason", report.getReason());
            reportsConfig.set(path + ".date", report.getDate().getTime());
            reportsConfig.set(path + ".resolved", report.isResolved());
        }

        try {
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Não foi possível salvar os reports: " + e.getMessage());
        }
    }

    private void notifyStaff(Report report) {
        String notification = plugin.getConfigManager().getMessage("system.report-notification")
                .replace("{reporter}", report.getReporter())
                .replace("{reported}", report.getReported());

        String sound = plugin.getConfigManager().getNotificationSound();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("discordreports.view")) {
                player.sendMessage(notification);
                if (!sound.equals("NONE")) {
                    try {
                        player.playSound(player.getLocation(), Sound.valueOf(sound), 1.0f, 1.0f);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Som inválido configurado: " + sound);
                    }
                }
            }
        }
    }
} 