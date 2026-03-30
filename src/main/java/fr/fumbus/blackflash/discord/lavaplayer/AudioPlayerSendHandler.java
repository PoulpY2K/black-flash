package fr.fumbus.blackflash.discord.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */

@RequiredArgsConstructor
public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private final MutableAudioFrame frame = new MutableAudioFrame();

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(frame);
    }

    @Override
    public @Nullable ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(frame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
