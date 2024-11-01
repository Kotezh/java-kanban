package taskTracker.service;

import taskTracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_SIZE = 10;
    private List<Task> viewedTasks = new ArrayList<>();

    public InMemoryHistoryManager() {
        this.viewedTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (viewedTasks.size() == MAX_SIZE) {
            viewedTasks.remove(0);
        }
        viewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return viewedTasks;
    }
}
