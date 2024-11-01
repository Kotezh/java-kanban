package taskTracker.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description, TaskState taskState) {
        super(name, description, taskState);
    }

    public ArrayList<Subtask> getSubtasks() {
        return this.subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
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

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }
}
