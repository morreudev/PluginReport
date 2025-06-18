package com.morreudev.discord.reports.discord;

import com.morreudev.discord.reports.DiscordReports;
import com.morreudev.discord.reports.models.Report;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiscordBot {

    private final DiscordReports plugin;
    private final SimpleDateFormat dateFormat;
    private JDA jda;
    private String channelId;

    public DiscordBot(DiscordReports plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        setupBot();
    }

    private void setupBot() {
        String token = plugin.getConfigManager().getConfig().getString("discord.token");
        this.channelId = plugin.getConfigManager().getConfig().getString("discord.channel-id");

        if (token == null || token.isEmpty() || token.equals("COLOQUE_SEU_TOKEN_AQUI")) {
            plugin.getLogger().warning("Token do bot não configurado!");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .build();

            jda.awaitReady();
            plugin.getLogger().info("Bot do Discord conectado com sucesso!");

        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao conectar ao Discord: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendReport(Report report) {
        if (jda == null) {
            plugin.getLogger().warning("Bot não está conectado ao Discord!");
            return;
        }

        if (channelId == null || channelId.isEmpty() || channelId.equals("COLOQUE_O_ID_DO_CANAL_AQUI")) {
            plugin.getLogger().warning("ID do canal do Discord não configurado!");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                TextChannel channel = jda.getTextChannelById(channelId);
                if (channel == null) {
                    plugin.getLogger().warning("Canal do Discord não encontrado! ID: " + channelId);
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Novo Report");
                embed.setColor(Color.decode(plugin.getConfigManager().getConfig().getString("embed.color", "#FF0000")));
                
                // Campos do embed
                if (plugin.getConfigManager().getConfig().getBoolean("fields.reporter", true)) {
                    embed.addField("Reportado por", ChatColor.stripColor(report.getReporter()), true);
                }
                
                if (plugin.getConfigManager().getConfig().getBoolean("fields.reported", true)) {
                    embed.addField("Jogador Reportado", ChatColor.stripColor(report.getReported()), true);
                }
                
                if (plugin.getConfigManager().getConfig().getBoolean("fields.reason", true)) {
                    embed.addField("Motivo", ChatColor.stripColor(report.getReason()), false);
                }
                
                if (plugin.getConfigManager().getConfig().getBoolean("fields.date", true)) {
                    embed.addField("Data", dateFormat.format(new Date()), true);
                }

                // Thumbnail
                String thumbnail = plugin.getConfigManager().getConfig().getString("embed.thumbnail");
                if (thumbnail != null && !thumbnail.isEmpty()) {
                    embed.setThumbnail(thumbnail);
                }

                // Footer
                embed.setFooter("Sistema de Reports • " + Bukkit.getServerName() + " • " + dateFormat.format(new Date()));

                // Timestamp
                if (plugin.getConfigManager().getConfig().getBoolean("embed.timestamp", true)) {
                    embed.setTimestamp(new Date().toInstant());
                }

                channel.sendMessageEmbeds(embed.build()).queue(
                    success -> {

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Player reporter = Bukkit.getPlayer(report.getReporter());
                            if (reporter != null && reporter.isOnline()) {
                                reporter.sendMessage("§aReport enviado com sucesso!");
                            }
                        });
                    },
                    error -> plugin.getLogger().warning("Erro ao enviar mensagem para o Discord: " + error.getMessage())
                );

            } catch (Exception e) {
                plugin.getLogger().severe("Erro ao enviar mensagem para o Discord: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public JDA getJda() {
        return jda;
    }
} 