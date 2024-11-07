package pl.pollub.frontend.service;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import lombok.Getter;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.event.OnEvent;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.util.JsonUtil;
import pl.pollub.frontend.util.SimpleJsonBuilder;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Injectable
public class GroupsService {
    @Inject
    private EventEmitter eventEmitter;
    @Inject
    private HttpService httpService;
    @Inject
    private PollingService pollingService;

    @Getter
    private List<Group> groups = new ArrayList<>();

    @OnEvent(EventType.LOGIN)
    public void updateGroups() {
        groups = fetchGroups();
    }

    @PostInitialize
    public void startPooling() {
        pollingService.addTask(() -> {
            groups = fetchGroups();
            Platform.runLater(() -> eventEmitter.emit(EventType.GROUPS_UPDATE));
        });
    }

    public Group getGroupById(long groupId) {
        for (Group group : groups) {
            if (group.getId().equals(groupId))
                return group;
        }

        return null;
    }

    public List<Group> fetchGroups() {
        HttpResponse<String> response = httpService.get("/groups");
        Type type = new TypeToken<List<Group>>() {
            // nothing here
        }.getType();

        return JsonUtil.GSON.fromJson(response.body(), type);
    }

    public HttpResponse<String> addGroup(String name, String color) {
        return httpService.post("/groups", SimpleJsonBuilder.empty()
                .add("name", name)
                .add("color", color)
                .build());
    }
}
