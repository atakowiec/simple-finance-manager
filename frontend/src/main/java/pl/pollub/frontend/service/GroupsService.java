package pl.pollub.frontend.service;

import com.google.gson.reflect.TypeToken;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;
import pl.pollub.frontend.model.group.Group;
import pl.pollub.frontend.util.JsonUtil;
import pl.pollub.frontend.util.SimpleJsonBuilder;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

@Injectable
public class GroupsService {
    @Inject
    private HttpService httpService;

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
