package ru.feytox.etherology.util.delayedTask;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTaskManager {
    private final List<DelayedTask> taskList = new ArrayList<>();

    /**
     * Executes all delayed tasks in the task list.
     */
    public void tickTasks() {
        taskList.removeIf(task -> {
            boolean result = task.tick();
            if (result) task.execute();
            return !result;
        });
    }

    protected void addTask(DelayedTask task) {
        taskList.add(task);
    }
}
