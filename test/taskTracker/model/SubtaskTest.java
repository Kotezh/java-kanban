package taskTracker.model;

import org.junit.jupiter.api.Test;
import taskTracker.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    // проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void shouldBeNegativeWhenIdIsNotEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1);
        Subtask subtask_1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_1 = taskManager.createNewSubtask(subtask_1);
        Subtask subtask_2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_2 = taskManager.createNewSubtask(subtask_2);

        assertNotEquals(addedSubtask_1, addedSubtask_2);
    }

    @Test
    public void shouldBePositiveWhenIdIsEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1);
        Subtask subtask_1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, addedEpic_1.getId());
        Subtask subtask_2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, addedEpic_1.getId());

        assertEquals(subtask_1, subtask_2);
    }

}