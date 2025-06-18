package com.morreudev.discord.reports.listeners;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.models.Report;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MenuListener implements Listener {
    private final DiscordReports plugin;
    private final Set<UUID> invisibleStaff;

    public MenuListener(DiscordReports plugin) {
        this.plugin = plugin;
        this.invisibleStaff = new HashSet<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) return;


        if (event.getView().getTitle().equals("§8Sistema de Reports")) {
            event.setCancelled(true);

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§cReportar Jogador")) {
                    plugin.getMenuManager().openPlayerSelector(player);
                } else if (displayName.equals("§aVer Reports") && player.hasPermission("discordreports.view")) {
                    plugin.getMenuManager().openReportListMenu(player);
                } else if (displayName.equals("§9Configurar Discord") && player.hasPermission("discordreports.admin")) {
                    plugin.getMenuManager().openDiscordConfigMenu(player);
                }
            }
        }

        else if (event.getView().getTitle().equals("§8Selecionar Jogador")) {
            event.setCancelled(true);

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§cVoltar")) {
                    plugin.getMenuManager().openMainMenu(player);
                } else if (displayName.startsWith("§e")) {
                    String targetName = ChatColor.stripColor(displayName);
                    plugin.getMenuManager().openReasonSelector(player, targetName);
                }
            }
        }

        else if (event.getView().getTitle().equals("§8Selecionar Motivo")) {
            event.setCancelled(true);

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§cVoltar")) {
                    plugin.getMenuManager().openPlayerSelector(player);
                } else if (displayName.startsWith("§c")) {
                    String reason = ChatColor.stripColor(displayName);
                    String targetName = plugin.getMenuManager().getReportTarget(player);

                    if (targetName != null) {
                        Report report = new Report(player.getName(), targetName, reason);
                        plugin.getReportManager().addReport(report);
                        plugin.getDiscordBot().sendReport(report);
                        plugin.getMenuManager().removeReportTarget(player);
                        player.closeInventory();
                        player.sendMessage(plugin.getConfigManager().getMessage("success.report-sent"));
                    }
                }
            }
        }

        else if (event.getView().getTitle().equals("§8Reports Pendentes")) {
            event.setCancelled(true);

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName() && clickedItem.getItemMeta().hasLore()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§cVoltar")) {
                    plugin.getMenuManager().openMainMenu(player);
                } else if (displayName.startsWith("§cReport #") && player.hasPermission("discordreports.view")) {

                    for (String line : clickedItem.getItemMeta().getLore()) {
                        if (line.startsWith("§7Reportado: §f")) {
                            String reportedName = line.replace("§7Reportado: §f", "");
                            Player reportedPlayer = Bukkit.getPlayer(reportedName);

                            if (reportedPlayer != null && reportedPlayer.isOnline()) {

                                setStaffInvisible(player);
                                player.teleport(reportedPlayer.getLocation());
                                player.sendMessage("§aTeleportado para " + reportedPlayer.getName() + " §7(Você está invisível)");
                                player.sendMessage("§7Use §e/v §7para ficar visível novamente");
                                

                                for (Player staff : Bukkit.getOnlinePlayers()) {
                                    if (staff.hasPermission("discordreports.view") && !staff.equals(player)) {
                                        staff.sendMessage("§e" + player.getName() + " §7está verificando o report de §e" + reportedPlayer.getName());
                                    }
                                }
                            } else {
                                player.sendMessage("§cJogador offline ou não encontrado!");
                            }
                            break;
                        }
                    }
                }
            }
        }

        else if (event.getView().getTitle().equals("§8Configurar Discord")) {
            event.setCancelled(true);

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                if (displayName.equals("§cVoltar")) {
                    plugin.getMenuManager().openMainMenu(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.getMenuManager().isSettingWebhook(player)) {
            event.setCancelled(true);
            String webhook = event.getMessage();
            

            if (!webhook.startsWith("https://discord.com/api/webhooks/")) {
                player.sendMessage(ChatColor.RED + "URL do webhook inválida! Deve começar com 'https://discord.com/api/webhooks/'");
                return;
            }

            plugin.getMenuManager().handleWebhookSetup(player, webhook);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (invisibleStaff.contains(player.getUniqueId())) {
            setStaffVisible(player);
        }
    }

    public void setStaffInvisible(Player staff) {
        invisibleStaff.add(staff.getUniqueId());
        

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.hasPermission("discordreports.view")) {
                online.hidePlayer(staff);
            }
        }
        

        staff.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
    }

    public void setStaffVisible(Player staff) {
        invisibleStaff.remove(staff.getUniqueId());
        

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(staff);
        }
        

        staff.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public boolean isStaffInvisible(Player staff) {
        return invisibleStaff.contains(staff.getUniqueId());
    }
} 