package taskTracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void shouldGetTaskName() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        String taskName = task_1.getName();
        assertEquals("Купить чай", taskName);
    }

    @Test
    void shouldSetTaskName() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        task_1.setName("Новое название задачи");
        String taskName = task_1.getName();
        assertEquals("Новое название задачи", taskName);
    }

    @Test
    void shouldGetTaskDescription() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        String taskDescription = task_1.getDescription();
        assertEquals("Вкусный", taskDescription);
    }

    @Test
    void shouldSetTaskDescription() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        task_1.setDescription("Новое описание задачи");
        String taskDescription = task_1.getDescription();
        assertEquals("Новое описание задачи", taskDescription);
    }

    @Test
    void shouldGetTaskState() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        TaskState taskState = task_1.getTaskState();
        assertEquals(TaskState.NEW, taskState);
    }

    @Test
    void shouldSetTaskState() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        task_1.setTaskState(TaskState.DONE);
        TaskState taskState = task_1.getTaskState();
        assertEquals(TaskState.DONE, taskState);
    }
}