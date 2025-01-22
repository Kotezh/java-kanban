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

    public Subtask(String name, String description, TaskState taskState, String startTime, long duration, int epicId) {
        super(name, description, taskState, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return this.epicId;
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
