package ru.feytox.etherology.util.delayedTask;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.World;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DelayedTask {
    private int age = 0;
    private final int maxAge;
    @NonNull
    private final Runnable taskBody;
    private boolean canceled = false;

    /**
     * Determines if the age of the object has exceeded the maximum age allowed
     *
     * @return true if the age is greater than or equal to the maximum age, false otherwise
     */
    public boolean tick() {
        return canceled || age++ >= maxAge;
    }

    public void execute() {
        if (!canceled) taskBody.run();
    }

    public void cancel() {
        canceled = true;
    }

    /**
     * Creates a task to be executed after a specified ticks using a delayed task object.
     *
     * @param  world      the world in which the task is to be executed
     * @param  ticks      the number of ticks to delay the task execution (1 tick = 50ms)
     * @param  taskBody   the code to be executed as the task
     * @return created task
     */
    // TODO: 19.12.2024 refactor
    public static DelayedTask createTask(World world, int ticks, @NonNull Runnable taskBody) {
        DelayedTask task = new DelayedTask(ticks, taskBody);
        AbstractTaskManager taskManager = world.isClient() ? ClientTaskManager.INSTANCE : ServerTaskManager.INSTANCE;
        taskManager.addTask(task);
        return task;
    }

    /**
     * Creates a task to be executed after a specified number of milliseconds in the given world.
     * 
     * @param world         the world in which the task will be executed
     * @param milliseconds  the number of milliseconds to wait before executing the task
     * @param taskBody      the body of the task to be executed
     */
    public static void createTaskWithMs(World world, int milliseconds, @NonNull Runnable taskBody) {
        int ticks = Math.round(milliseconds / 50f);
        createTask(world, ticks, taskBody);
    }
}
