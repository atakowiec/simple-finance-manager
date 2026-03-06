package pl.pollub.backend.group.export;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resolves export commands by requested format.
 */
@Component
public class ExportCommandFactory {
    private final Map<String, ExportCommand> commandsByFormat;

    public ExportCommandFactory(List<ExportCommand> commands) {
        this.commandsByFormat = commands.stream()
                .collect(Collectors.toMap(command -> command.getFormat().toLowerCase(Locale.ROOT), Function.identity()));
    }

    public ExportCommand getCommand(String format) {
        return commandsByFormat.get(format.toLowerCase(Locale.ROOT));
    }
}

