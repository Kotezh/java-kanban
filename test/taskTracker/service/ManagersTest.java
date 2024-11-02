package taskTracker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    @Test
    void shouldBeHistoryManagerCreated() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    void shouldBeInMemoryTaskManagerCreated() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }
}