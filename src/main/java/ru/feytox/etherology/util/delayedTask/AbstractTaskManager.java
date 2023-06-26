package ru.feytox.etherology.util.delayedTask;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTaskManager {
    private List<DelayedTask> taskList = new ArrayList<>();

    /**
     * Executes all delayed tasks in the task list.
     */
    public void tickTasks() {
        List<DelayedTask> newTaskList = new ArrayList<>();
        for (DelayedTask task : taskList) {
            boolean result = task.tick();

            if (result) task.execute();
            else newTaskList.add(task);
        }
        taskList = newTaskList;
    }

    protected void addTask(DelayedTask task) {
        taskList.add(task);
    }
}
