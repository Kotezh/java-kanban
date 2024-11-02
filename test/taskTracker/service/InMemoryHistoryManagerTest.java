package taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.model.Task;
import taskTracker.model.TaskState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    //  убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void shouldBeTaskAddedToHistory() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
    }

    @Test
    void shouldGetHistory() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
    }
}