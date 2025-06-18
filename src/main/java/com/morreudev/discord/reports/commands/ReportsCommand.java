package com.morreudev.discord.reports.commands;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.models.Report;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Collection;

public class ReportsCommand implements CommandExecutor {

    private final DiscordReports plugin;
    private final SimpleDateFormat dateFormat;

    public ReportsCommand(DiscordReports plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("discordreports.view")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return true;
        }

        Collection<Report> reports = plugin.getReportManager().getReports();

        if (reports.isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getMessage("system.no-reports"));
            return true;
        }

        sender.sendMessage(plugin.getConfigManager().getMessage("system.report-list-header"));
        
        int id = 1;
        for (Report report : reports) {
            sender.sendMessage(
                plugin.getConfigManager().getMessage("system.report-list-format")
                    .replace("{id}", String.valueOf(id))
                    .replace("{reported}", report.getReported())
                    .replace("{reason}", report.getReason())
            );
            id++;
        }
        
        sender.sendMessage(plugin.getConfigManager().getMessage("system.report-list-footer"));
        return true;
    }
} 