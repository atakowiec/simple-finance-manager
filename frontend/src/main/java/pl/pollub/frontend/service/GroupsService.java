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
import pl.pollub.frontend.model.group.GroupStatEntry;
import pl.pollub.frontend.model.transaction.TransactionCategory;
import pl.pollub.frontend.util.JsonUtil;
import pl.pollub.frontend.util.SimpleJsonBuilder;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Injectable
public class GroupsService {
    @Inject
    private EventEmitter eventEmitter;
    @Inject
    private HttpService httpService;
    @Inject
    private CategoryService categoryService;
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

    private List<GroupStatEntry> prepareByDateData(String jsonData) {
        Type type = new TypeToken<Map<String, Double>>() {
            // nothing here
        }.getType();

        Map<String, Double> statMap = JsonUtil.GSON.fromJson(jsonData, type);
        List<GroupStatEntry> result = new ArrayList<>();

        // fill in the missing days with 0.0
        LocalDate now = LocalDate.now();
        LocalDate currentDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);

        while (currentDate.getMonthValue() == now.getMonthValue()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

            result.add(new GroupStatEntry(currentDate.format(formatter), statMap.getOrDefault(currentDate.toString(), 0.0)));

            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

    public List<GroupStatEntry> fetchLastMonthIncomeStats(Long groupId) {
        HttpResponse<String> response = httpService.get("/incomes/" + groupId + "/stats/by-day");
        return prepareByDateData(response.body());
    }

    public List<GroupStatEntry> fetchLastMonthExpenseStats(Long groupId) {
        HttpResponse<String> response = httpService.get("/expenses/" + groupId + "/stats/by-day");
        return prepareByDateData(response.body());
    }

    public List<GroupStatEntry> fetchLastMonthCategoryExpenseStats(Long groupId) {
        HttpResponse<String> response = httpService.get("/expenses/" + groupId + "/stats/categories");
        Type type = new TypeToken<Map<String, Double>>() {
            // nothing here
        }.getType();

        Map<String, Double> statMap = JsonUtil.GSON.fromJson(response.body(), type);
        List<GroupStatEntry> result = new ArrayList<>();

        for (Map.Entry<String, Double> entry : statMap.entrySet()) {
            int categoryId = Integer.parseInt(entry.getKey());

            GroupStatEntry statEntry = new GroupStatEntry(categoryService.getExpenseCategoryById(categoryId).getName(), entry.getValue());
            result.add(statEntry);
        }

        // fill all missing categories with 0.0
        for (TransactionCategory category : categoryService.getExpenseCategories()) {
            if (result.stream().noneMatch(entry -> entry.getKey().equals(category.getName()))) {
                result.add(new GroupStatEntry(category.getName(), 0.0));
            }
        }

        return result;
    }
}
