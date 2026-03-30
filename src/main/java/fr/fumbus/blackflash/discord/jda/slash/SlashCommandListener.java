package fr.fumbus.blackflash.discord.jda.slash;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */

@Log4j2
@Component
@RequiredArgsConstructor
public class SlashCommandListener extends ListenerAdapter {
    // TODO: add all music logic here, and split into separate services if needed

    @PostConstruct
    public void init() {
        log.info("SlashCommandListener initialized and ready to handle slash commands.");
    }
}
