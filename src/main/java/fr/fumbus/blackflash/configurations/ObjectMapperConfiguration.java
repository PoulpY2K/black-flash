package fr.fumbus.blackflash.configurations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

/**
 * @author Jérémy Laurent <poulpy2k>
 * @see "https://github.com/poulpy2k"
 */
@Configuration
public class ObjectMapperConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StringTrimmingDeserializer());

        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule())
                .registerModule(module);
    }

    public static class StringTrimmingDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            return value != null ? value.trim() : null;
        }
    }
}
