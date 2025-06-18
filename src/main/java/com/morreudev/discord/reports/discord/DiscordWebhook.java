package com.morreudev.discord.reports.discord;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.models.Report;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiscordWebhook {

    private final DiscordReports plugin;
    private final SimpleDateFormat dateFormat;

    public DiscordWebhook(DiscordReports plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    public void sendReport(Report report) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String channelId = plugin.getConfigManager().getConfig().getString("discord.channel-id");
                if (channelId == null || channelId.isEmpty()) {
                    plugin.getLogger().warning("ID do canal do Discord não configurado!");
                    return;
                }

                TextChannel channel = plugin.getDiscordBot().getJda().getTextChannelById(channelId);
                if (channel == null) {
                    plugin.getLogger().warning("Canal do Discord não encontrado! ID: " + channelId);
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(plugin.getConfigManager().getConfig().getString("embed.title", "Novo Report"));
                embed.setColor(Color.decode(plugin.getConfigManager().getConfig().getString("embed.color", "#FF0000")));

                // Campos do embed
                if (plugin.getConfigManager().getConfig().getBoolean("fields.reporter", true)) {
                    embed.addField(plugin.getConfigManager().getMessage("discord.embed-reporter"), report.getReporter(), true);
                }

                if (plugin.getConfigManager().getConfig().getBoolean("fields.reported", true)) {
                    embed.addField(plugin.getConfigManager().getMessage("discord.embed-reported"), report.getReported(), true);
                }

                if (plugin.getConfigManager().getConfig().getBoolean("fields.reason", true)) {
                    embed.addField(plugin.getConfigManager().getMessage("discord.embed-reason"), report.getReason(), false);
                }

                if (plugin.getConfigManager().getConfig().getBoolean("fields.server", true)) {
                    embed.addField(plugin.getConfigManager().getMessage("discord.embed-server"), Bukkit.getServerName(), true);
                }

                if (plugin.getConfigManager().getConfig().getBoolean("fields.date", true)) {
                    embed.addField(plugin.getConfigManager().getMessage("discord.embed-date"), dateFormat.format(new Date()), true);
                }

                // Thumbnail
                String thumbnail = plugin.getConfigManager().getConfig().getString("embed.thumbnail");
                if (thumbnail != null && !thumbnail.isEmpty()) {
                    embed.setThumbnail(thumbnail);
                }

                // Footer
                String footer = plugin.getConfigManager().getConfig().getString("embed.footer");
                if (footer != null && !footer.isEmpty()) {
                    embed.setFooter(footer);
                }

                // Timestamp
                if (plugin.getConfigManager().getConfig().getBoolean("embed.timestamp", true)) {
                    embed.setTimestamp(new Date().toInstant());
                }

                channel.sendMessageEmbeds(embed.build()).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("Erro ao enviar mensagem para o Discord: " + error.getMessage())
                );

            } catch (Exception e) {
                plugin.getLogger().severe("Erro ao enviar mensagem para o Discord!");
                e.printStackTrace();
            }
        });
    }
} 