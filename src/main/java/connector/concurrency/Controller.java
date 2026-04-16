package connector.concurrency;

import models.service.config.NodeAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Controller {
    private static final int MIN_THREAD_NUM = 1;
    private static final int MAX_THREAD_NUM = 16;

    private static final int MIN_POLL_INTERVAL_SECONDS = 0;
    private static final int MAX_POLL_INTERVAL_SECONDS = 86400;

    private final ScheduledExecutorService scheduler;
    private final ApiHandler handler;
    private final List<ScheduledFuture<?>> activeTasks;

    private boolean running;

    public Controller(int nThreads, ApiHandler handler) {
        if (nThreads < MIN_THREAD_NUM || nThreads > MAX_THREAD_NUM)
            throw new IllegalArgumentException("invalid value for the number of threads");

        if (handler == null)
            throw new NullPointerException("handler can not be null");

        this.scheduler = Executors.newScheduledThreadPool(nThreads);
        this.handler = handler;
        this.activeTasks = new ArrayList<>();
        this.running = false;
    }

    public void startPoll(List<NodeAPI> apis, int intervalSeconds) {
        if (apis == null)
            throw new NullPointerException("api list can not be null");

        if (apis.isEmpty())
            throw new IllegalArgumentException("api list can not be empty");

        if (intervalSeconds < MIN_POLL_INTERVAL_SECONDS || intervalSeconds > MAX_POLL_INTERVAL_SECONDS)
            throw new IllegalArgumentException("invalid time for the polling period");

        if (running) {
            System.out.println("polling already started");
            return;
        }

        activeTasks.clear();

        for (NodeAPI api : apis) {
            ApiTask task = new ApiTask(handler, api);

            ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
                    task,
                    0,
                    0,
                    TimeUnit.SECONDS
            );

            activeTasks.add(future);
        }

        running = true;
    }

    public void stopPoll() {
        if (!running) {
            System.out.println("Polling is not running");
            return;
        }

        for (ScheduledFuture<?> future : activeTasks)
            future.cancel(false);

        activeTasks.clear();
        running = false;
    }

    public void shutdown() {
        stopPoll();
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS))
                scheduler.shutdownNow();

        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            System.out.println("shutdown program has been fail: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }
}