package tasktracker.service;

import tasktracker.model.Task;

import java.util.List;


public interface HistoryManager<T extends Task> {
    void add(T task);

    void remove(int id);

    List<T> getHistory();
}
