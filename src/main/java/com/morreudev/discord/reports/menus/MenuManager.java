package com.morreudev.discord.reports.menus;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.models.Report;
import com.morreudev.discord.reports.utils.HeadUtil;
import com.morreudev.discord.reports.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MenuManager {
    private final DiscordReports plugin;
    private final Map<UUID, String> webhookSetupPlayers;
    private final Map<UUID, String> reportTargetPlayers;

    public MenuManager(DiscordReports plugin) {
        this.plugin = plugin;
        this.webhookSetupPlayers = new HashMap<>();
        this.reportTargetPlayers = new HashMap<>();
    }

    public void openMainMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§8Sistema de Reports");


        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                .setName("§7")
                .build();
        
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, glass);
        }

        boolean hasViewPermission = player.hasPermission("discordreports.view");
        boolean hasAdminPermission = player.hasPermission("discordreports.admin");

        ItemStack reportButton = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.report-button")))
                .setName("§cReportar Jogador")
                .setLore(
                    "§7Clique para reportar um",
                    "§7jogador que quebrou as regras",
                    "",
                    "§eClique para abrir"
                )
                .build();

        if (!hasViewPermission && !hasAdminPermission) {

            menu.setItem(13, reportButton);
        } else {

            menu.setItem(11, reportButton);

            if (hasViewPermission) {
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

            if (hasAdminPermission) {
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
        }

        player.openInventory(menu);
    }

    public void openPlayerSelector(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§8Selecionar Jogador");


        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                .setName("§7")
                .build();
        
        for (int i = 0; i < 9; i++) {
            menu.setItem(i, glass);
            menu.setItem(i + 45, glass);
        }


        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < Math.min(onlinePlayers.size(), 36); i++) {
            Player target = onlinePlayers.get(i);
            if (target != player) {
                ItemStack head = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setName("§e" + target.getName())
                        .setLore(
                            "§7Clique para selecionar",
                            "§7este jogador"
                        )
                        .setSkullOwner(target.getName())
                        .build();
                menu.setItem(i + 9, head);
            }
        }


        ItemStack back = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.back-button")))
                .setName("§cVoltar")
                .setLore(
                    "§7Voltar para o menu",
                    "§7principal"
                )
                .build();
        menu.setItem(49, back);

        player.openInventory(menu);
    }

    public void openReasonSelector(Player player, String target) {
        Inventory menu = Bukkit.createInventory(null, 36, "§8Selecionar Motivo");


        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                .setName("§7")
                .build();
        
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, glass);
        }


        ItemStack hack = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.reasons.hack")))
                .setName("§cHack/Cheat")
                .setLore(
                    "§7Reportar por uso de",
                    "§7programas ilegais",
                    "",
                    "§eClique para selecionar"
                )
                .build();
        menu.setItem(10, hack);

        ItemStack ofensa = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.reasons.ofensa")))
                .setName("§cOfensa")
                .setLore(
                    "§7Reportar por ofensas",
                    "§7ou palavras inadequadas",
                    "",
                    "§eClique para selecionar"
                )
                .build();
        menu.setItem(12, ofensa);

        ItemStack spam = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.reasons.spam")))
                .setName("§cSpam")
                .setLore(
                    "§7Reportar por spam",
                    "§7no chat",
                    "",
                    "§eClique para selecionar"
                )
                .build();
        menu.setItem(14, spam);

        ItemStack ameaca = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.reasons.ameaca")))
                .setName("§cAmeaça")
                .setLore(
                    "§7Reportar por ameaças",
                    "§7ou intimidação",
                    "",
                    "§eClique para selecionar"
                )
                .build();
        menu.setItem(16, ameaca);

        ItemStack toxico = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.reasons.toxico")))
                .setName("§cComportamento Tóxico")
                .setLore(
                    "§7Reportar por comportamento",
                    "§7tóxico ou inadequado",
                    "",
                    "§eClique para selecionar"
                )
                .build();
        menu.setItem(20, toxico);

        ItemStack bug = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.reasons.bug")))
                .setName("§cAbuso de Bug")
                .setLore(
                    "§7Reportar por abuso",
                    "§7de bugs/falhas",
                    "",
                    "§eClique para selecionar"
                )
                .build();
        menu.setItem(24, bug);


        ItemStack back = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.back-button")))
                .setName("§cVoltar")
                .setLore(
                    "§7Voltar para a seleção",
                    "§7de jogador"
                )
                .build();
        menu.setItem(31, back);

        reportTargetPlayers.put(player.getUniqueId(), target);
        player.openInventory(menu);
    }

    public void openReportListMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§8Reports Pendentes");


        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                .setName("§7")
                .build();
        
        for (int i = 0; i < 9; i++) {
            menu.setItem(i, glass);
            menu.setItem(i + 45, glass);
        }


        Collection<Report> reports = plugin.getReportManager().getReports();
        int slot = 9;

        for (Report report : reports) {
            Player reportedPlayer = Bukkit.getPlayer(report.getReported());
            boolean isOnline = reportedPlayer != null && reportedPlayer.isOnline();

            List<String> lore = new ArrayList<>();
            lore.add("§7Reportado: §f" + report.getReported());
            lore.add("§7Por: §f" + report.getReporter());
            lore.add("§7Motivo: §f" + report.getReason());
            lore.add("§7Data: §f" + new Date(report.getDate().getTime()));
            lore.add("");
            lore.add(isOnline ? "§aJogador Online" : "§cJogador Offline");
            if (isOnline) {
                lore.add("§eClique para se teleportar");
            }

            ItemStack reportIcon = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.report-icon")))
                    .setName("§cReport #" + (slot - 8))
                    .setLore(lore)
                    .setGlowing(isOnline)
                    .build();
            menu.setItem(slot++, reportIcon);

            if (slot > 44) break;
        }


        ItemStack back = new ItemBuilder(HeadUtil.getCustomHead(plugin.getConfigManager().getConfig().getString("heads.back-button")))
                .setName("§cVoltar")
                .setLore(
                    "§7Voltar para o menu",
                    "§7principal"
                )
                .build();
        menu.setItem(49, back);

        player.openInventory(menu);
    }

    public void openWebhookSetupMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, ChatColor.RED + "Configurar Webhook");
        FileConfiguration config = plugin.getConfigManager().getConfig();

        ItemStack configItem = createItem(
            HeadUtil.getCustomHead(config.getString("heads.discord-settings", "")),
            "&9&lConfigurar Webhook",
            Arrays.asList(
                "&7Clique para configurar o webhook",
                "&7do Discord. Você precisará colar",
                "&7o link no chat."
            )
        );
        menu.setItem(13, configItem);


        ItemStack backButton = createItem(
            HeadUtil.getCustomHead(config.getString("heads.back-button", "")),
            "&c&lVoltar",
            Collections.singletonList("&7Voltar ao menu principal")
        );
        menu.setItem(22, backButton);

        ItemStack glass = createItem(Material.STAINED_GLASS_PANE, " ", Collections.emptyList(), (byte) 14);
        for (int i = 0; i < 27; i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, glass);
            }
        }

        player.openInventory(menu);
    }

    public void startWebhookSetup(Player player) {
        webhookSetupPlayers.put(player.getUniqueId(), "waiting");
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "Por favor, cole o link do webhook do Discord no chat:");
    }

    public boolean isSettingWebhook(Player player) {
        return webhookSetupPlayers.containsKey(player.getUniqueId());
    }

    public void handleWebhookSetup(Player player, String webhook) {
        webhookSetupPlayers.remove(player.getUniqueId());
        plugin.getConfigManager().getConfig().set("discord.webhook-url", webhook);
        plugin.getConfigManager().saveConfig();
        player.sendMessage(ChatColor.GREEN + "Webhook configurado com sucesso!");
    }

    public String getReportTarget(Player player) {
        return reportTargetPlayers.get(player.getUniqueId());
    }

    public void removeReportTarget(Player player) {
        reportTargetPlayers.remove(player.getUniqueId());
    }

    public void openDiscordConfigMenu(Player player) {
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
                    "§7principal"
                )
                .build();
        menu.setItem(26, back);

        player.openInventory(menu);
    }

    private ItemStack createItem(ItemStack item, String name, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(coloredLore);
        
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        return createItem(material, name, lore, (byte) 0);
    }

    private ItemStack createItem(Material material, String name, List<String> lore, byte data) {
        ItemStack item = new ItemStack(material, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(coloredLore);
        
        item.setItemMeta(meta);
        return item;
    }

    private String formatToken(String token) {

        return token;
    }

    private String formatChannelId(String channelId) {

        return channelId;
    }
} 