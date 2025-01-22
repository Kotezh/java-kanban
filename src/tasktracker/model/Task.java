package tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskState taskState;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description, TaskState taskState) {
        this.name = name;
        this.description = description;
        this.taskState = taskState;
    }

    public Task(int id) {
        this.taskState = TaskState.NEW;
        this.id = id;
    }

    public Task(String name, String description, TaskState taskState, String startTime, long duration) {
        this.name = name;
        this.description = description;
        this.taskState = taskState;
        this.startTime = LocalDateTime.parse(startTime, dateTimeFormatter);
        this.duration = Duration.ofMinutes(duration);
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public LocalDateTime getEndTime(){
        return startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public static <T extends Task> int compareTime(T a, T b) {
        return a.getStartTime().isBefore(b.getStartTime()) ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", taskState=" + taskState +
                '}';
    }
}
