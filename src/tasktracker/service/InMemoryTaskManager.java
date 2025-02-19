package tasktracker.service;

import tasktracker.exceptions.DateTimeException;
import tasktracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter;
    private Map<Integer, Task> tasks = new HashMap<Integer, Task>();
    private Map<Integer, Subtask> subtasks = new HashMap<Integer, Subtask>();
    private Map<Integer, Epic> epics = new HashMap<Integer, Epic>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private Set<Task> treeSet = new TreeSet<>(Task::compareTime);

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
            Task removedTask = tasks.get(id);
            tasks.remove(id);
            historyManager.remove(id);
            removeFromTreeSet(removedTask);
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
                removeFromTreeSet(subtask);
            }
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = this.getEpicById(id);
        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
        if (epics.containsKey(id)) {
            epicSubtasks.forEach(subtask -> {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            });

            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();
        removeTypeFromTreeSet(TaskType.TASK);
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            int epicId = subtask.getEpicId();
            Epic epic = getEpicById(epicId);
            ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
            epicSubtasks.clear();
            historyManager.remove(subtask.getId());
            updateEpicState(epicId);
        });
        removeTypeFromTreeSet(TaskType.SUBTASK);
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        removeTypeFromTreeSet(TaskType.SUBTASK);
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task createNewTask(Task task) {
        checkCrossedTime(task, "На это время уже запланирована другая задача");
        int oldId = task.getId();
        int id = this.getCurrentId(oldId);
        task.setId(id);
        tasks.put(task.getId(), task);
        addToTreeSet(task);
        return task;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        checkCrossedTime(subtask, "На это время уже запланирована другая подзадача");
        int oldId = subtask.getId();
        int subtaskId = this.getCurrentId(oldId);

        Epic epic = getEpicById(subtask.getEpicId());

        subtask.setId(subtaskId);
        subtasks.put(subtask.getId(), subtask);
        if (epic != null && epic.getId() == subtask.getEpicId()) {
            epic.addSubtask(subtask);
            updateEpicState(epic.getId());
        }
        addToTreeSet(subtask);
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
    public Task updateTask(Task task) throws DateTimeException{
        checkCrossedTime(task, "На это время уже запланирована другая задача");
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            return task;
        }
        updateTreeSet(task);
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws DateTimeException {
        checkCrossedTime(subtask, "На это время уже запланирована другая подзадача");
        int subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) {
            subtasks.put(subtaskId, subtask);
            Epic epic = getEpicById(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicState(epic.getId());
            return subtask;
        }
        updateTreeSet(subtask);
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            setEpicTime(epic);
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

        if (epicSubtasks.isEmpty()) {
            epic.setTaskState(TaskState.NEW);
            return;
        }
        boolean isAllSubtasksNew = epicSubtasks.stream().allMatch(epicSubtask -> epicSubtask.getTaskState() == TaskState.NEW);
        boolean isAllSubtasksDone = epicSubtasks.stream().allMatch(epicSubtask -> epicSubtask.getTaskState() == TaskState.DONE);

        if (isAllSubtasksNew) {
            epic.setTaskState(TaskState.NEW);
        } else if (isAllSubtasksDone) {
            epic.setTaskState(TaskState.DONE);
        } else {
            epic.setTaskState(TaskState.IN_PROGRESS);
        }
        setEpicTime(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(treeSet);
    }

    public <T extends Task> void checkCrossedTime(T task, String message) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        if (startTime == null || endTime == null) {
            throw new DateTimeException(message);
        }

        boolean isCrossed = treeSet.stream().filter(treeTask -> treeTask.getId() != task.getId()).anyMatch(treeTask -> {
            LocalDateTime treeTaskStart = treeTask.getStartTime();
            LocalDateTime treeTaskEnd = treeTask.getEndTime();
            if (treeTaskEnd.isAfter(startTime) && treeTaskEnd.isBefore(endTime)) {
                return true;
            }
            if ((treeTaskStart.isAfter(startTime) || treeTaskStart.isEqual(startTime)) && !treeTaskStart.isAfter(endTime)) {
                return true;
            }

            return false;
        });
        if (isCrossed) {
            throw new DateTimeException(message);
        }
    }

    public void addToTreeSet(Task task) {
        treeSet.add(task);
    }

    public <T extends Task> void updateTreeSet(T task) {
        if (task == null) {
            return;
        }

        Task oldTask = (task.getTaskType() == TaskType.TASK ? tasks : subtasks).get(task.getId());
        removeFromTreeSet(oldTask);
        addToTreeSet(task);
    }

    protected List<Task> removeTypeFromTreeSet(TaskType taskType) {
        return treeSet.stream().filter(task -> task.getTaskType() != taskType).toList();
    }

    public <T extends Task> void removeFromTreeSet(T task) {
        treeSet.remove(task);
    }

    protected void setEpicTime(Epic epic) {
        if (epic == null) {
            return;
        }

        List<Subtask> subtasks = getSubtasksByEpic(epic);

        LocalDateTime startTime = subtasks.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = subtasks.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = subtasks.stream()
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        if (startTime != null) {
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
        }

        if (!duration.isZero()) {
            epic.setDuration(duration);
        }
    }
}
