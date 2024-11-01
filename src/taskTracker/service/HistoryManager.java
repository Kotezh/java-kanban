package taskTracker.service;

import taskTracker.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
