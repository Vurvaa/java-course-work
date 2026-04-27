package connector.concurrency;

import models.service.config.NodeAPI;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Controller {
    private static final int MIN_THREAD_NUM = 1;
    private static final int MAX_THREAD_NUM = 16;

    private static final int MIN_POLL_INTERVAL_SECONDS = 0;
    private static final int MAX_POLL_INTERVAL_SECONDS = 86400;

    private static final int CHECK_DELAY_MILLISECONDS = 100;

    private final ScheduledExecutorService scheduler;
    private final List<ScheduledFuture<?>> activeTasks;
    private final Map<String, Long> lastPollTimeMap;
    private final ApiHandler handler;

    private boolean running;

    public Controller(int nThreads, ApiHandler handler) {
        if (nThreads < MIN_THREAD_NUM || nThreads > MAX_THREAD_NUM)
            throw new IllegalArgumentException("invalid value for the number of threads");

        if (handler == null)
            throw new NullPointerException("handler can not be null");

        this.scheduler = Executors.newScheduledThreadPool(nThreads);
        this.handler = handler;
        this.activeTasks = new ArrayList<>();
        this.lastPollTimeMap = new HashMap<>();
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
        lastPollTimeMap.clear();

        int id = 0;
        for (NodeAPI api : apis) {
            String apiKey = api.name() + id++;
            ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
                    () -> {
                        if (!canHandleApi(apiKey, intervalSeconds))
                            return;

                        try {
                            handler.handleApi(api);
                            markApiHandled(apiKey);
                        } catch (IOException e) {
                            markApiHandled(apiKey);
                            System.out.printf("failed receiving a response from the api: %s - %s\n",
                                                api.name(), e.getMessage());
                        } catch (Exception e) {
                            System.out.println("api processing failed: " + e.getMessage());
                        }
                    },
                    0,
                    CHECK_DELAY_MILLISECONDS,
                    TimeUnit.MILLISECONDS
            );

            activeTasks.add(future);
        }

        running = true;
    }

    private boolean canHandleApi(String api, int intervalSeconds) {
        long now = System.currentTimeMillis();
        long intervalMillis = intervalSeconds * 1000L;

        synchronized (lastPollTimeMap) {
            if (intervalSeconds != 0) {
                long last = lastPollTimeMap.getOrDefault(api, 0L);

                if (now - last < intervalMillis)
                    return false;
            }

            return true;
        }
    }

    private void markApiHandled(String api) {
        synchronized (lastPollTimeMap) {
            lastPollTimeMap.put(api, System.currentTimeMillis());
        }
    }

    public void stopPoll() {
        if (!running) {
            System.out.println("Polling is not running");
            return;
        }

        for (ScheduledFuture<?> future : activeTasks)
            future.cancel(false);

        activeTasks.clear();
        lastPollTimeMap.clear();
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