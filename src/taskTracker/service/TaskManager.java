package taskTracker.service;

import taskTracker.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected int idCounter;
    private HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<Integer, Subtask>();
    private HashMap<Integer, Epic> epics = new HashMap<Integer, Epic>();

    private int getCurrentId() {
        return ++idCounter;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = getSubtaskById(id);
            if (subtask != null) {
                subtasks.remove(id);
                Epic epic = getEpicById(subtask.getEpicId());
                epic.removeSubtask(subtask);
                updateEpicState(epic.getId());
            }
        }
    }

    public void removeEpic(int id) {
        Epic epic = this.getEpicById(id);
        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
        if (epics.containsKey(id)) {
            for (Subtask subtask : epicSubtasks) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
        }
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int epicId = subtask.getEpicId();
            Epic epic = getEpicById(epicId);
            ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
            epicSubtasks.clear();
            updateEpicState(epicId);
        }
        subtasks.clear();
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Task createNewTask(Task task) {
        int id = this.getCurrentId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Subtask createNewSubtask(Subtask subtask) {
        int subtaskId = this.getCurrentId();
        Epic epic = getEpicById(subtask.getEpicId());
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        epic.addSubtask(subtask);
        updateEpicState(epic.getId());
        return subtask;
    }

    public Epic createNewEpic(Epic epic) {
        int id = this.getCurrentId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    public Task updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            return task;
        }
        return null;
    }

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

}
