package taskTracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void shouldGetTaskName() {
        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        String taskName = task1.getName();
        assertEquals("Купить чай", taskName);
    }

    @Test
    void shouldSetTaskName() {
        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        task1.setName("Новое название задачи");
        String taskName = task1.getName();
        assertEquals("Новое название задачи", taskName);
    }

    @Test
    void shouldGetTaskDescription() {
        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        String taskDescription = task1.getDescription();
        assertEquals("Вкусный", taskDescription);
    }

    @Test
    void shouldSetTaskDescription() {
        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        task1.setDescription("Новое описание задачи");
        String taskDescription = task1.getDescription();
        assertEquals("Новое описание задачи", taskDescription);
    }

    @Test
    void shouldGetTaskState() {
        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        TaskState taskState = task1.getTaskState();
        assertEquals(TaskState.NEW, taskState);
    }

    @Test
    void shouldSetTaskState() {
        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        task1.setTaskState(TaskState.DONE);
        TaskState taskState = task1.getTaskState();
        assertEquals(TaskState.DONE, taskState);
    }
}