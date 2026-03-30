package fr.fumbus.blackflash.discord.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */

public class GuildMusicManager {
    public final AudioPlayer player;
    public final TrackScheduler trackScheduler;

    public GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        trackScheduler = new TrackScheduler();
        player.addListener(trackScheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}
