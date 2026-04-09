package fr.fumbus.blackflash.discord.slash;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import fr.fumbus.blackflash.lavalink.AudioLoader;
import fr.fumbus.blackflash.lavalink.GuildMusicManager;
import fr.fumbus.blackflash.lavalink.LoopMode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static fr.fumbus.blackflash.discord.slash.SlashCommandConstants.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */

@Log4j2
@Getter
@Component
@RequiredArgsConstructor
public class SlashCommandListener extends ListenerAdapter {
    private final LavalinkClient lavalink;
    private final SlashCommandRegistry slashCommandRegistry;
    private final Map<Long, GuildMusicManager> musicManagers = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        lavalink.on(TrackStartEvent.class).subscribe(event ->
                {
                    log.trace("{}: track started: {}", event.getNode().getName(), event.getTrack().getInfo());
                    Optional.ofNullable(musicManagers.get(event.getGuildId())).ifPresent(
                            manager -> manager.getTrackScheduler().onTrackStart(event)
                    );
                }
        );

        lavalink.on(TrackEndEvent.class).subscribe(event ->
                {
                    log.trace("{}: track ended: {}, reason: {}", event.getNode().getName(), event.getTrack().getInfo(), event.getEndReason());
                    Optional.ofNullable(musicManagers.get(event.getGuildId())).ifPresent(
                            manager -> manager.getTrackScheduler().onTrackEnd(event)
                    );
                }
        );
    }

    @Override
    public void onReady(@NonNull ReadyEvent event) {
        log.info("{} is ready!", event.getJDA().getSelfUser().getAsTag());

        event.getJDA().updateCommands()
                .addCommands(slashCommandRegistry.getCommands())
                .queue();
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {
        Guild guild = getGuild(event);
        Member member = getMember(event);
        if (checkIfMemberNotInVoiceChannel(member)) {
            event.reply(MESSAGE_MEMBER_NOT_IN_VOICE_CHANNEL).setEphemeral(true).queue();
            return;
        }

        String commandName = event.getFullCommandName();
        switch (commandName) {
            case COMMAND_JOIN:
                if (isBotInVoiceChannel(guild)) {
                    event.reply(MESSAGE_BOT_ALREADY_IN_VOICE_CHANNEL).setEphemeral(true).queue();
                    return;
                }
                joinChannel(event);
                break;
            case COMMAND_STOP:
                if (!isBotInVoiceChannel(guild)) {
                    event.reply(MESSAGE_BOT_NOT_IN_VOICE_CHANNEL).setEphemeral(true).queue();
                    return;
                }
                getOrCreateMusicManager(guild.getIdLong()).stop();
                event.reply("Stopped the current track!").queue();
                break;
            case COMMAND_LEAVE:
                if (!isBotInVoiceChannel(guild)) {
                    event.reply(MESSAGE_BOT_NOT_IN_VOICE_CHANNEL).setEphemeral(true).queue();
                    return;
                }
                event.getJDA().getDirectAudioController().disconnect(guild);
                musicManagers.remove(guild.getIdLong());
                event.reply("Leaving the channel!").queue();
                break;
            case COMMAND_PLAY:
                playSong(event, guild);
                break;
            case COMMAND_LOOP:
                if (!isBotInVoiceChannel(guild)) {
                    event.reply(MESSAGE_BOT_NOT_IN_VOICE_CHANNEL).setEphemeral(true).queue();
                    return;
                }
                handleLoop(event, guild);
                break;
            default:
                log.warn("Received an unknown slash command interaction: {}", commandName);
                event.reply("Unknown command!").setEphemeral(true).queue();
        }
    }

    private void handleLoop(SlashCommandInteractionEvent event, Guild guild) {
        final GuildMusicManager manager = getOrCreateMusicManager(guild.getIdLong());
        final LoopMode newMode = manager.getTrackScheduler().getLoopMode().next();
        manager.getTrackScheduler().setLoopMode(newMode);

        String message = switch (newMode) {
            case TRACK -> "🔂 Loop track enabled!";
            case QUEUE -> "🔁 Loop queue enabled!";
            default -> "Loop disabled!";
        };

        event.reply(message).queue();
    }

    private void playSong(SlashCommandInteractionEvent event, Guild guild) {
        if (isBotInVoiceChannel(guild)) {
            event.deferReply(false).queue();
        } else {
            joinChannel(event);
        }

        final String query = Optional.ofNullable(event.getOption("query"))
                .map(OptionMapping::getAsString)
                .orElse("");
        final long guildId = guild.getIdLong();
        final Link link = lavalink.getOrCreateLink(guildId);
        final var manager = getOrCreateMusicManager(guildId);
        link.loadItem(query).subscribe(new AudioLoader(event, manager));
    }

    private void joinChannel(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        if (nonNull(member)) {
            final GuildVoiceState memberVoiceState = member.getVoiceState();
            if (nonNull(memberVoiceState) && memberVoiceState.inAudioChannel() && nonNull(memberVoiceState.getChannel())) {
                event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
            }
            getOrCreateMusicManager(member.getGuild().getIdLong());
        }
        event.reply("Joining your channel!").queue();
    }

    @Synchronized
    private GuildMusicManager getOrCreateMusicManager(long guildId) {
        return musicManagers.computeIfAbsent(guildId, id -> new GuildMusicManager(id, lavalink));
    }

    private static boolean isBotInVoiceChannel(Guild guild) {
        return Optional.ofNullable(guild.getSelfMember().getVoiceState())
                .map(GuildVoiceState::inAudioChannel)
                .orElse(false);
    }

    private static @NonNull Member getMember(SlashCommandInteractionEvent event) {
        return Optional
                .ofNullable(event.getMember())
                .orElseThrow(
                        () -> {
                            log.error("Received a slash command interaction without a member context!");
                            return new IllegalStateException("Received a slash command interaction without a member context!");
                        }
                );
    }

    private static boolean checkIfMemberNotInVoiceChannel(Member member) {
        return isNull(member.getVoiceState()) || !member.getVoiceState().inAudioChannel();
    }

    private static @NonNull Guild getGuild(SlashCommandInteractionEvent event) {
        return Optional
                .ofNullable(event.getGuild())
                .orElseThrow(
                        () -> {
                            log.error("Received a slash command interaction without a guild context!");
                            return new IllegalStateException("Received a slash command interaction without a guild context!");
                        }
                );
    }
}
