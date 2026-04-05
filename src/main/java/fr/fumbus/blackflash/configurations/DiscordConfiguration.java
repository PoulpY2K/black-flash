package fr.fumbus.blackflash.configurations;

import fr.fumbus.blackflash.discord.jda.slash.SlashCommandListener;
import fr.fumbus.blackflash.discord.jda.slash.SlashCommandRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */

@Log4j2
@Configuration
@RequiredArgsConstructor
public class DiscordConfiguration {

    private final List<GatewayIntent> enabledIntents = List.of(
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_VOICE_STATES
    );

    private final SlashCommandRegistry slashCommandRegistry;
    private final SlashCommandListener listener;

    @Value("${discord.token}")
    private String token;

    @Value("${discord.activity}")
    private String activity;

    @PostConstruct
    public void initializeJDA() {
        try {
            log.info("Initializing JDA, registering listeners and commands...");
            JDA jdaClient = buildJDA();
            registerCommands(jdaClient);
            log.info("JDA initialized successfully.");
        } catch (InvalidTokenException e) {
            log.error("Invalid token, JDA could not initialize: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error("JDA initialization was interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("An unexpected error occurred while initializing JDA: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void registerCommands(JDA jda) {
        jda.updateCommands()
                .addCommands(slashCommandRegistry.getCommands())
                .queue();
    }

    private @NonNull JDA buildJDA() throws InterruptedException {
        return JDABuilder.createDefault(token)
                .enableIntents(enabledIntents)
                .enableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY)
                .setActivity(Activity.listening(activity))
                .setAutoReconnect(true)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.include(100))
                .addEventListeners(listener)
                .build()
                .awaitReady();
    }
}
