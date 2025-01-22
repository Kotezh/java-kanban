package tasktracker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskState taskState) {
        super(name, description, taskState);
    }

    public Epic(int id) {
        super(id);
    }

    public ArrayList<Subtask> getSubtasks() {
        return this.subtasks;
    }

    public void addSubtask(Subtask subtask) {
        if (!this.subtasks.isEmpty()) {
            for (int i = 0; i < this.subtasks.size(); i++) {
                Subtask currentSubtask = this.subtasks.get(i);
                if (subtask.id == currentSubtask.getId()) {
                    this.subtasks.remove(currentSubtask);
                }
            }
        }
        this.subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        this.subtasks.remove(subtask);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskState=" + taskState +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }
}
