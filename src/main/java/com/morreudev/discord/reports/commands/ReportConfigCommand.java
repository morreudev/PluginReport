package com.morreudev.discord.reports.commands;

import com.morreudev.discord.reports.DiscordReports;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportConfigCommand implements CommandExecutor {
    private final DiscordReports plugin;

    public ReportConfigCommand(DiscordReports plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando só pode ser usado por jogadores!");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("discordreports.admin")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        plugin.getMenuManager().openWebhookSetupMenu(player);
        return true;
    }
} 