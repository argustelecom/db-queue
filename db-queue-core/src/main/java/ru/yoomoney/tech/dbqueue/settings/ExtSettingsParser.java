package ru.yoomoney.tech.dbqueue.settings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Parser for {@link ExtSettings}.
 */
public class ExtSettingsParser {
    private final Supplier<ExtSettings.Builder> defaultSettings;
    private final List<String> errorMessages;

    /**
     * Constructor
     *
     * @param defaultSettings default settings
     * @param errorMessages   list of error messages
     */
    ExtSettingsParser(@Nonnull Supplier<ExtSettings.Builder> defaultSettings,
            @Nonnull List<String> errorMessages) {
        this.defaultSettings = Objects.requireNonNull(defaultSettings, "defaultSettings");
        this.errorMessages = Objects.requireNonNull(errorMessages, "errorMessages");
    }

    /**
     * Parse settings
     *
     * @param settings raw settings
     * @return settings or empty object in case of failure
     */
    @Nonnull
    public ExtSettings parseExtSettings(Map<String, String> settings) {
        String extSettingsPrefix = QueueConfigsReader.SETTING_ADDITIONAL + '.';
        ExtSettings.Builder extSettings = defaultSettings.get();
        for (Map.Entry<String, String> property : settings.entrySet()) {
            if (property.getKey().startsWith(extSettingsPrefix)) {
                extSettings.withSetting(property.getKey().substring(extSettingsPrefix.length()), property.getValue());
            }
        }
        return extSettings.build();
    }
}
