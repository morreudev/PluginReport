package com.morreudev.discord.reports;

import com.morreudev.discord.reports.config.ConfigManager;
import com.morreudev.discord.reports.discord.DiscordBot;
import com.morreudev.discord.reports.managers.ReportManager;
import com.morreudev.discord.reports.menus.MenuManager;
import com.morreudev.discord.reports.listeners.MenuListener;
import com.morreudev.discord.reports.commands.ReportConfigCommand;
import com.morreudev.discord.reports.commands.VisibilityCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DiscordReports extends JavaPlugin {

    private ConfigManager configManager;
    private ReportManager reportManager;
    private DiscordBot discordBot;
    private MenuManager menuManager;
    @Getter
    private MenuListener menuListener;

    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);
        this.reportManager = new ReportManager(this);
        this.discordBot = new DiscordBot(this);
        this.menuManager = new MenuManager(this);


        this.menuListener = new MenuListener(this);
        getServer().getPluginManager().registerEvents(menuListener, this);


        getCommand("report").setExecutor((sender, command, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player) {
                menuManager.openMainMenu((org.bukkit.entity.Player) sender);
            }
            return true;
        });

        getCommand("discordreports").setExecutor(new ReportConfigCommand(this));
        getCommand("v").setExecutor(new VisibilityCommand(this));

        getLogger().info("DiscordReports ativado com sucesso!");
    }

    @Override
    public void onDisable() {

        if (reportManager != null) {
            reportManager.saveReports();
        }


        if (discordBot != null) {
            discordBot.shutdown();
        }

        getLogger().info("DiscordReports desativado com sucesso!");
    }
} 