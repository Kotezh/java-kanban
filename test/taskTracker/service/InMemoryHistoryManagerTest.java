package taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.model.Task;
import taskTracker.model.TaskState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager = Managers.getDefault();


    @BeforeEach
    void beforeEach() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        Task addedTask_1 = taskManager.createNewTask(task_1);
        Task touchedTask_1 = taskManager.getTaskById(addedTask_1.getId());
        Task task_2 = new Task("Посмотреть вебинар", "Запись эфира на capoeiraskills", TaskState.NEW);
        Task addedTask_2 = taskManager.createNewTask(task_2);
        Task touchedTask_2 = taskManager.getTaskById(addedTask_2.getId());
        Task task_3 = new Task("Сдать задание", "Успеть вовремя", TaskState.NEW);
        Task addedTask_3 = taskManager.createNewTask(task_3);
        Task touchedTask_3 = taskManager.getTaskById(addedTask_3.getId());
    }

    //  убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void shouldBeTasksAddedToHistory() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(3, history.size(), "Количество записей не совпадает.");
        assertEquals("Купить чай", history.getFirst().getName(), "Значение не совпадает.");
        assertEquals("Запись эфира на capoeiraskills", history.get(1).getDescription(), "Значение не совпадает.");
        assertEquals(3, history.getLast().getId(), "Значение не совпадает.");
    }

    @Test
    void shouldBeFirstTaskRemovedFromHistory() {
        taskManager.removeTask(1);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals("Посмотреть вебинар", history.getFirst().getName(), "Значение не совпадает.");
        assertEquals(2, history.size(), "Количество записей не совпадает.");
    }

    @Test
    void shouldBeLastTaskRemovedFromHistory() {
        taskManager.removeTask(3);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals("Посмотреть вебинар", history.getLast().getName(), "Значение не совпадает.");
        assertEquals(2, history.size(), "Количество записей не совпадает.");
    }

    @Test
    void shouldGetHistory() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(3, history.size(), "Количество записей не совпадает.");
    }
}