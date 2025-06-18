package com.morreudev.discord.reports.menus;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.utils.HeadUtil;
import com.morreudev.discord.reports.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ReportMenu {
    private final DiscordReports plugin;

    public ReportMenu(DiscordReports plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§8Sistema de Reports");


        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                .setName("§7")
                .build();
        
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, glass);
        }


        ItemStack reportButton = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.report-button")))
                .setName("§cReportar Jogador")
                .setLore(
                    "§7Clique para reportar um",
                    "§7jogador que quebrou as regras",
                    "",
                    "§eClique para abrir"
                )
                .build();
        menu.setItem(11, reportButton);


        if (player.hasPermission("discordreports.view")) {
            ItemStack viewReports = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.view-reports")))
                    .setName("§aVer Reports")
                    .setLore(
                        "§7Visualize todos os reports",
                        "§7pendentes do servidor",
                        "",
                        "§eClique para abrir"
                    )
                    .build();
            menu.setItem(13, viewReports);
        }


        if (player.hasPermission("discordreports.admin")) {
            ItemStack discordSettings = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.discord-settings")))
                    .setName("§9Configurar Discord")
                    .setLore(
                        "§7Configure as mensagens e",
                        "§7o canal do Discord",
                        "",
                        "§eClique para abrir"
                    )
                    .build();
            menu.setItem(15, discordSettings);
        }

        player.openInventory(menu);
    }
} 