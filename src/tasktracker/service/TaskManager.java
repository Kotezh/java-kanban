package tasktracker.service;

import tasktracker.model.Epic;
import tasktracker.model.Subtask;
import tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    ArrayList<Subtask> getSubtasksByEpic(Epic epic);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    Task createNewTask(Task task);

    Subtask createNewSubtask(Subtask subtask);

    Epic createNewEpic(Epic epic);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
