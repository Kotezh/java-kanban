package tasktracker.service;

import tasktracker.exceptions.ManagerSaveException;
import tasktracker.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        if (file == null) return;
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            List<Task> allTasks = new ArrayList<>(getAllTasks().size() + getAllSubtasks().size() + getAllEpics().size());
            allTasks.addAll(getAllTasks());
            allTasks.addAll(getAllSubtasks());
            allTasks.addAll(getAllEpics());

            for (Task task : allTasks) {
                fileWriter.write(toString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("При записи файла произошла ошибка");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bfReader = new BufferedReader(new FileReader(file))) {
            while (bfReader.ready()) {
                String line = bfReader.readLine().trim();
                if (line.startsWith("id") || line.isEmpty()) {
                    continue;
                }
                Task task = fileBackedTaskManager.fromString(line);
                if (task != null) {
                    TaskType taskType = task.getTaskType();
                    switch (taskType) {
                        case TASK -> fileBackedTaskManager.createNewTask(task);
                        case SUBTASK -> fileBackedTaskManager.createNewSubtask((Subtask) task);
                        case EPIC -> fileBackedTaskManager.createNewEpic((Epic) task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время чтения файла произошла ошибка");
        }

        return fileBackedTaskManager;
    }

    private <T extends Task> String toString(T task) {
        String epicId = task instanceof Subtask ? "," + ((Subtask) task).getEpicId() : "";
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getTaskState() + "," + task.getDescription() + epicId + "\n";
    }

    private <T extends Task> T fromString(String value) {
        String[] fields = value.split(",");
        int taskId = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String taskName = fields[2];
        TaskState taskState = TaskState.valueOf(fields[3]);
        String taskDescription = fields[4];
        switch (taskType) {
            case TaskType.TASK -> {
                Task task = new Task(taskId);
                task.setName(taskName);
                task.setTaskState(taskState);
                task.setDescription(taskDescription);

                return (T) task;
            }
            case TaskType.SUBTASK -> {
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(taskId);
                subtask.setName(taskName);
                subtask.setTaskState(taskState);
                subtask.setDescription(taskDescription);
                subtask.setEpicId(epicId);

                return (T) subtask;
            }
            case TaskType.EPIC -> {
                Epic epic = new Epic(taskId);
                epic.setName(taskName);
                epic.setDescription(taskDescription);

                return (T) epic;
            }
        }
        return null;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task createNewTask(Task task) {
        super.createNewTask(task);
        save();
        return task;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }


}
