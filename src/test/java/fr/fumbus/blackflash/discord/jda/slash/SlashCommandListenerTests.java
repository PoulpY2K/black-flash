package fr.fumbus.blackflash.discord.jda.slash;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SlashCommandListenerTests {

    // RETURNS_DEEP_STUBS lets the reactive chain .on(...).subscribe(...) work without NPE
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    LavalinkClient lavalinkClient;

    @InjectMocks
    SlashCommandListener listener;

    @Test
    void slashCommandListener_extendsListenerAdapter() {
        assertThat(listener).isInstanceOf(ListenerAdapter.class);
    }

    @Test
    void slashCommandListener_musicManagersInitiallyEmpty() {
        assertThat(listener.getMusicManagers()).isEmpty();
    }

    @Test
    void init_doesNotThrow() {
        assertDoesNotThrow(() -> listener.init());
    }

    @Test
    void init_subscribesToTrackStartAndTrackEndEvents() {
        listener.init();

        verify(lavalinkClient).on(TrackStartEvent.class);
        verify(lavalinkClient).on(TrackEndEvent.class);
    }
}

