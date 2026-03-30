package fr.fumbus.blackflash.discord.jda.slash;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import fr.fumbus.blackflash.discord.lavaplayer.GuildMusicManager;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;

/**
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */

@Log4j2
@AllArgsConstructor
public class SlashCommandListener extends ListenerAdapter {
    // TODO: add all music logic here, and split into separate services if needed
    private AudioPlayerManager playerManager;
    private Map<Long, GuildMusicManager> musicManagers;

    @PostConstruct
    public void init() {
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        log.info("SlashCommandListener initialized and ready to handle slash commands.");
    }
}
