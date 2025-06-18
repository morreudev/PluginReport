package com.morreudev.discord.reports.config;

import com.morreudev.discord.reports.DiscordReports;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final DiscordReports plugin;
    private File configFile;
    private File messagesFile;
    @Getter
    private FileConfiguration config;
    @Getter
    private FileConfiguration messages;

    public ConfigManager(DiscordReports plugin) {
        this.plugin = plugin;
        setupFiles();
    }

    private void setupFiles() {

        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);


        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadConfigs() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Não foi possível salvar config.yml!");
            e.printStackTrace();
        }
    }

    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Não foi possível salvar messages.yml!");
            e.printStackTrace();
        }
    }

    public String getMessage(String path) {
        String prefix = messages.getString("prefix", "&8[&6Reports&8] ");
        String message = messages.getString("messages." + path, "Mensagem não encontrada: " + path);
        

        if (path.startsWith("plugin.")) {
            if (path.contains("error") || path.contains("cooldown") || 
                path.equals("plugin.no-permission") || 
                path.equals("plugin.player-not-found") || 
                path.equals("plugin.report-yourself")) {
                message = "§c" + message;
            } else {
                message = "§a" + message;
            }
        }
        
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public String getMessageWithoutPrefix(String path) {
        String message = messages.getString("messages." + path, "Mensagem não encontrada: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getDiscordToken() {
        return config.getString("discord.token", "");
    }

    public String getDiscordChannelId() {
        return config.getString("discord.channel-id", "");
    }

    public int getReportCooldown() {
        return config.getInt("settings.report-cooldown", 300);
    }

    public int getMaxPendingReports() {
        return config.getInt("settings.max-pending-reports", 50);
    }

    public boolean isNotifyStaffEnabled() {
        return config.getBoolean("settings.notify-staff", true);
    }

    public String getNotificationSound() {
        return config.getString("settings.notification-sound", "LEVEL_UP");
    }
} 