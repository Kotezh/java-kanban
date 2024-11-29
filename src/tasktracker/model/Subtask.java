package tasktracker.model;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, TaskState taskState, int epicId) {
        super(name, description, taskState);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(Epic epic) {
        this.epicId = epic.getId();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }
}
