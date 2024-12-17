package tasktracker.service;

import tasktracker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter;
    private Map<Integer, Task> tasks = new HashMap<Integer, Task>();
    private Map<Integer, Subtask> subtasks = new HashMap<Integer, Subtask>();
    private Map<Integer, Epic> epics = new HashMap<Integer, Epic>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public int getCurrentId(int oldId) {
        if (oldId > 0) {
            idCounter = oldId;
        } else {
            ++idCounter;
        }
        return idCounter;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
        return epicSubtasks;
    }


    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    @Override
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = getSubtaskById(id);
            if (subtask != null) {
                subtasks.remove(id);
                historyManager.remove(id);
                Epic epic = getEpicById(subtask.getEpicId());
                epic.removeSubtask(subtask);
                updateEpicState(epic.getId());
            }
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = this.getEpicById(id);
        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
        if (epics.containsKey(id)) {
            for (Subtask subtask : epicSubtasks) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int epicId = subtask.getEpicId();
            Epic epic = getEpicById(epicId);
            ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
            epicSubtasks.clear();
            historyManager.remove(subtask.getId());
            updateEpicState(epicId);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task createNewTask(Task task) {
        int oldId = task.getId();
        int id = this.getCurrentId(oldId);
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        int oldId = subtask.getId();
        int subtaskId = this.getCurrentId(oldId);

        Epic epic = getEpicById(subtask.getEpicId());

        subtask.setId(subtaskId);
        subtasks.put(subtask.getId(), subtask);
        if (epic != null && epic.getId() == subtask.getEpicId()) {
            epic.addSubtask(subtask);
            updateEpicState(epic.getId());
        }
        return subtask;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        int oldId = epic.getId();
        int id = this.getCurrentId(oldId);

        epic.setId(id);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            return task;
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            subtasks.put(subtaskId, subtask);
            Epic epic = getEpicById(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicState(epic.getId());
            return subtask;
        }
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            epics.put(id, epic);
            return epic;
        }
        return null;
    }

    public void updateEpicState(int epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();

        if (epic == null) {
            return;
        }
        boolean isAllSubtasksNew = true;
        boolean isAllSubtasksDone = true;

        if (epicSubtasks.isEmpty()) {
            epic.setTaskState(TaskState.NEW);
            return;
        }

        for (Subtask epicSubtask : epicSubtasks) {
            if (epicSubtask.getTaskState() != TaskState.NEW) {
                isAllSubtasksNew = false;
            }
            if (epicSubtask.getTaskState() != TaskState.DONE) {
                isAllSubtasksDone = false;
            }
        }
        if (isAllSubtasksNew) {
            epic.setTaskState(TaskState.NEW);
        } else if (isAllSubtasksDone) {
            epic.setTaskState(TaskState.DONE);
        } else {
            epic.setTaskState(TaskState.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
