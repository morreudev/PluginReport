package com.morreudev.discord.reports.menus;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.utils.HeadUtil;
import com.morreudev.discord.reports.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DiscordConfigMenu {
    private final DiscordReports plugin;

    public DiscordConfigMenu(DiscordReports plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§8Configurar Discord");


        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                .setName("§7")
                .build();
        
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, glass);
        }


        boolean isConnected = plugin.getDiscordBot().getJda() != null;
        ItemStack status = new ItemBuilder(Material.INK_SACK, 1, (byte) (isConnected ? 10 : 8))
                .setName(isConnected ? "§aBot Conectado" : "§cBot Desconectado")
                .setLore(
                    "§7Status: " + (isConnected ? "§aOnline" : "§cOffline"),
                    "",
                    isConnected ? "§7O bot está funcionando" : "§7Configure o token do bot",
                    isConnected ? "§7corretamente!" : "§7nas configurações!"
                )
                .build();
        menu.setItem(11, status);


        ItemStack token = new ItemBuilder(Material.PAPER)
                .setName("§9Configurar Token")
                .setLore(
                    "§7Token atual: " + formatToken(plugin.getConfigManager().getConfig().getString("discord.token", "")),
                    "",
                    "§7Clique para configurar",
                    "§7o token do bot"
                )
                .build();
        menu.setItem(13, token);


        ItemStack channel = new ItemBuilder(Material.BOOK)
                .setName("§9Configurar Canal")
                .setLore(
                    "§7Canal atual: " + formatChannelId(plugin.getConfigManager().getConfig().getString("discord.channel-id", "")),
                    "",
                    "§7Clique para configurar",
                    "§7o ID do canal"
                )
                .build();
        menu.setItem(15, channel);

       
        ItemStack back = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.back-button")))
                .setName("§cVoltar")
                .setLore(
                    "§7Voltar para o menu",
                    "§7principal",
                    "",
                    "§eClique para voltar"
                )
                .build();
        menu.setItem(26, back);

        player.openInventory(menu);
    }

    private String formatToken(String token) {
        if (token == null || token.isEmpty() || token.equals("COLOQUE_SEU_TOKEN_AQUI")) {
            return "§cNão configurado";
        }
        return "§a" + token.substring(0, 8) + "...";
    }

    private String formatChannelId(String channelId) {
        if (channelId == null || channelId.isEmpty() || channelId.equals("COLOQUE_O_ID_DO_CANAL_AQUI")) {
            return "§cNão configurado";
        }
        return "§a" + channelId;
    }
} 