package fr.fumbus.blackflash.configurations;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import fr.fumbus.blackflash.discord.jda.slash.SlashCommandListener;
import fr.fumbus.blackflash.discord.jda.slash.SlashCommandRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
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
@RequiredArgsConstructor
@Configuration
public class DiscordConfiguration {

    private final List<GatewayIntent> enabledIntents = List.of(
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_VOICE_STATES
    );

    private final SlashCommandRegistry slashCommandRegistry;

    @Value("${discord.token}")
    private String token;

    @Value("${discord.activity}")
    private String activity;

    @PostConstruct
    public void initializeJDA() {
        try {
            log.info("Initializing JDA, registering listeners and commands...");
            registerCommands();
            log.info("JDA initialized successfully.");
        } catch (InvalidTokenException e) {
            log.error("Failed to initialize JDA: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error("JDA initialization was interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("An unexpected error occurred while initializing JDA: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void registerCommands() throws InterruptedException {
        JDA jda = buildJDA();
        jda.awaitReady();
        jda.updateCommands()
                .addCommands(slashCommandRegistry.getCommands())
                .queue();
    }

    private @NonNull JDA buildJDA() {
        return JDABuilder.createDefault(token)
                .setActivity(Activity.listening(activity))
                .setAudioModuleConfig(buildAudioModuleConfig())
                .setAutoReconnect(true)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.include(100))
                .enableIntents(enabledIntents)
                .addEventListeners(new SlashCommandListener())
                .enableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY)
                .build();
    }

    private static AudioModuleConfig buildAudioModuleConfig() {
        return new AudioModuleConfig().withAudioSendFactory(new NativeAudioSendFactory());
    }
}
