package pl.pollub.frontend.service;

import pl.pollub.frontend.injector.Injectable;

import java.util.List;

@Injectable
public class ColorsService {
    private final List<String> colors = List.of("#0000FF", "#00FF00", "#00FFFF", "#FF0000", "#FF00FF", "#FFFF00");

    public List<String> getColors() {
        return colors;
    }
}
