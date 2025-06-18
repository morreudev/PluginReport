package com.morreudev.discord.reports.commands;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.listeners.MenuListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VisibilityCommand implements CommandExecutor {
    private final DiscordReports plugin;

    public VisibilityCommand(DiscordReports plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser usado por jogadores!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("discordreports.view")) {
            player.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }

        MenuListener menuListener = plugin.getMenuListener();
        
        if (menuListener.isStaffInvisible(player)) {
            menuListener.setStaffVisible(player);
            player.sendMessage("§aAgora voce está visível para todos os jogadores!");
        } else {
            menuListener.setStaffInvisible(player);
            player.sendMessage("§aAgora voce está  invisível para jogadores sem permissão!");
        }

        return true;
    }
} 