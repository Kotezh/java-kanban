package taskTracker.model;

import org.junit.jupiter.api.Test;
import taskTracker.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void shouldBeNegativeWhenIdIsNotEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1);

        Epic epic_2 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_2 = taskManager.createNewEpic(epic_2);

        assertNotEquals(addedEpic_1, addedEpic_2);
    }

    @Test
    public void shouldBePositiveWhenIdIsEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic epic_2 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);

        assertEquals(epic_1, epic_2);
    }

}