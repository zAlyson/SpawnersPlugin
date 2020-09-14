package com.alysonsantos.aspect.utils.cache;

import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.scheduler.HelperExecutors;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CacheQueue<T> {

    @Getter
    private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();

    @Setter
    private Consumer<T> removalAction;
    private transient ScheduledFuture<?> updateTask;

    public CacheQueue() {
        updateTask = createTask();
    }

    public boolean stopQueue() {
        if (updateTask == null) return false;
        updateTask = null;
        return true;
    }

    public boolean startQueue() {
        if (updateTask != null) return false;
        updateTask = createTask();
        return true;
    }

    public void addItem(T item) {
        if (!queue.contains(item))
            queue.add(item);
    }

    public void removeItem(Predicate<T> predicate) {
        queue.removeIf(predicate);
    }

    public void updateAll() {
        while (!queue.isEmpty()) {
            update();
        }
    }

    private void update() {
        T item = queue.poll();
        if (item == null) return;

        if (removalAction == null) return;
        removalAction.accept(item);
    }

    private ScheduledFuture<?> createTask() {
        return HelperExecutors.asyncHelper()
                .scheduleAtFixedRate(
                        this::updateAll,
                        20,
                        5,
                        TimeUnit.SECONDS
                );
    }
}
