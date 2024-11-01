package taskTracker.model;

import org.junit.jupiter.api.Test;
import taskTracker.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    //    проверьте, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    public void shouldBePositiveWhenIdIsEquals() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        Task task_2 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        assertEquals(task_1, task_2);
    }

    @Test
    public void shouldBeNegativeWhenIdIsNotEquals() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        Task addedTask_1 = taskManager.createNewTask(task_1);
        Task task_2 = new Task("Посмотреть вебинар", "Запись эфира на capoeiraskills", TaskState.NEW);
        Task addedTask_2 = taskManager.createNewTask(task_2);

        assertNotEquals(addedTask_1, addedTask_2);
    }
}