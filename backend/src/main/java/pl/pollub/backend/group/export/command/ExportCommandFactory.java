package pl.pollub.backend.group.export.command;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// start factory
// start open close principle, Abstrakcja i sterowanie danymi
/**
 * Resolves export commands by requested format.
 */
public final class ExportCommandFactory {
    private static ExportCommandFactory instance;

    private final Map<String, ExportCommand> commandsByFormat;

    private ExportCommandFactory(List<ExportCommand> commands) {
        this.commandsByFormat = commands.stream()
                .collect(Collectors.toMap(command -> command.getFormat().toLowerCase(Locale.ROOT), Function.identity()));
    }

    public static synchronized void init(List<ExportCommand> commands) {
        if (instance == null) {
            instance = new ExportCommandFactory(commands);
        }
    }

    public static ExportCommandFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ExportCommandFactory is not initialized");
        }
        return instance;
    }

    public ExportCommand getCommand(String format) {
        return commandsByFormat.get(format.toLowerCase(Locale.ROOT));
    }
}

