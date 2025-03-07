package tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasktracker.model.Task.dateTimeFormatter;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager = Managers.getDefault();


    @BeforeEach
    void beforeEach() {
        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW, LocalDateTime.parse("01.10.2027 10:00", dateTimeFormatter), Duration.ofMinutes(30L));
        Task addedTask1 = taskManager.createNewTask(task1);
        Task touchedTask1 = taskManager.getTaskById(addedTask1.getId());
        Task task2 = new Task("Посмотреть вебинар", "Запись эфира на capoeiraskills", TaskState.NEW, LocalDateTime.parse("01.11.2027 10:00", dateTimeFormatter), Duration.ofMinutes(30L));
        Task addedTask2 = taskManager.createNewTask(task2);
        Task touchedTask2 = taskManager.getTaskById(addedTask2.getId());
        Task task3 = new Task("Сдать задание", "Успеть вовремя", TaskState.NEW, LocalDateTime.parse("02.11.2027 10:00", dateTimeFormatter), Duration.ofMinutes(30L));
        Task addedTask3 = taskManager.createNewTask(task3);
        Task touchedTask3 = taskManager.getTaskById(addedTask3.getId());
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