package telegram.Services.HttpWorkers.Updaters;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateFetcher {
    public static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void startFetchingUpdates() {
        executorService.scheduleAtFixedRate(() -> {
            System.out.println("Получение обновлений...");
            GetUpdates.getUpdates();
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopFetchingUpdates() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
