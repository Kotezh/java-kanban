package tasktracker.model;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, TaskState taskState, int epicId) {
        super(name, description, taskState);
        this.epicId = epicId;
    }

    public Subtask(int id) {
        super(id);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskState=" + taskState +
                '}';
    }
}
