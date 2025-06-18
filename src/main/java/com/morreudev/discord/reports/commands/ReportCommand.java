package com.morreudev.discord.reports.commands;

import com.morreudev.discord.reports.DiscordReports;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {

    private final DiscordReports plugin;

    public ReportCommand(DiscordReports plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.no-console"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("discordreports.report")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getMessage("help.report"));
            return true;
        }

        String targetName = args[0];
        if (targetName.equalsIgnoreCase(player.getName())) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.self-report"));
            return true;
        }


        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        String finalReason = reason.toString().trim();

        if (finalReason.length() < 3) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.too-short-reason"));
            return true;
        }

        // Cria o report
        plugin.getReportManager().createReport(player, targetName, finalReason);
        return true;
    }
} 