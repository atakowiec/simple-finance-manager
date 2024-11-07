package pl.pollub.frontend.service;

import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.injector.Injectable;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class PollingService {
    @Inject
    private AuthService authService;

    private final List<Runnable> tasks = new ArrayList<>();

    public void addTask(Runnable runnable) {
        tasks.add(runnable);
    }

    @PostInitialize
    public void startPooling() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);

                    if (!authService.isLoggedIn())
                        continue;

                    for (Runnable task : tasks) {
                        task.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
