package fr.fumbus.blackflash.discord.jda.slash;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Registry that builds and holds all Discord slash commands.
 * Commands are built once at instantiation and cached for reuse.
 *
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */

@Getter
@Component
public class SlashCommandRegistry {

    private final List<CommandData> commands;

    public SlashCommandRegistry() {
        this.commands = List.of(
                buildHelpCommand(),
                buildJoinCommand(),
                buildPlayCommand(),
                buildSkipCommand(),
                buildLoopCommand(),
                buildShuffleCommand(),
                buildLeaveCommand(),
                buildStopCommand()
        );
    }

    private static @NonNull SlashCommandData buildPlayCommand() {
        return Commands.slash("play", "Play a song or a playlist from a URL or search query")
                .setContexts(InteractionContextType.GUILD)
                .addOption(OptionType.STRING, "query", "URL or search query", true);
    }

    private static @NonNull SlashCommandData buildSkipCommand() {
        return Commands.slash("skip", "Skip the current track")
                .setContexts(InteractionContextType.GUILD);
    }

    private static @NonNull SlashCommandData buildStopCommand() {
        return Commands.slash("stop", "Stop playback and clear the queue")
                .setContexts(InteractionContextType.GUILD);
    }

    private static @NonNull SlashCommandData buildShuffleCommand() {
        return Commands.slash("shuffle", "Shuffle the playlist")
                .setContexts(InteractionContextType.GUILD);
    }

    private static @NonNull SlashCommandData buildLoopCommand() {
        return Commands.slash("loop", "Loop the current track or playlist")
                .setContexts(InteractionContextType.GUILD);
    }

    private static @NonNull SlashCommandData buildHelpCommand() {
        return Commands.slash("help", "Display help information")
                .setContexts(InteractionContextType.GUILD);
    }

    private static @NonNull SlashCommandData buildLeaveCommand() {
        return Commands.slash("leave", "Leave the voice channel")
                .setContexts(InteractionContextType.GUILD);
    }

    private static @NonNull SlashCommandData buildJoinCommand() {
        return Commands.slash("join", "Join the voice channel")
                .setContexts(InteractionContextType.GUILD);
    }
}

