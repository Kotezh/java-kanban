package taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.model.Epic;
import taskTracker.model.Subtask;
import taskTracker.model.Task;
import taskTracker.model.TaskState;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void beforeEach() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        Task addedTask_1 = taskManager.createNewTask(task_1); // id=1
        Task task_2 = new Task("Посмотреть вебинар", "Запись эфира на capoeiraskills", TaskState.NEW);
        Task addedTask_2 = taskManager.createNewTask(task_2); // id=2

        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1); // id=3
        Subtask subtask_1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_1 = taskManager.createNewSubtask(subtask_1); // id=4
        Subtask subtask_2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_2 = taskManager.createNewSubtask(subtask_2); // id=5

        Epic epic_2 = new Epic("Работа", "Здесь будут задачи по работе", TaskState.NEW);
        Epic addedEpic_2 = taskManager.createNewEpic(epic_2); // id=6
        Subtask subtask_3 = new Subtask("Добить таску", "Исправить валидацию формы и значение в поле Название магазина", TaskState.NEW, addedEpic_2.getId());
        Subtask addedSubtask_3 = taskManager.createNewSubtask(subtask_3); // id=7
    }

    @Test
    void shouldBeTaskWithId1() {
        Task task = taskManager.getTaskById(1);
        assertNotNull(task, "Задача отсутствует");
        assertEquals(1, task.getId());
        assertEquals("Купить чай", task.getName());
    }

    @Test
    void shouldBeSubtaskWithId4() {
        Subtask subtask_1 = taskManager.getSubtaskById(4);
        assertNotNull(subtask_1, "Задача отсутствует");
        assertEquals(4, subtask_1.getId());
        assertEquals("Купить продукты", subtask_1.getName());
    }

    @Test
    void shouldBeEpicWithId3() {
        Epic epic_1 = taskManager.getEpicById(3);
        assertNotNull(epic_1, "Задача отсутствует");
        assertEquals(3, epic_1.getId());
        assertEquals("Дом", epic_1.getName());
    }

    @Test
    void shouldBeSubtasksByEpicId3() {
        Epic addedEpic_1 = taskManager.getEpicById(3);
        ArrayList<Subtask> epic1Subtasks = taskManager.getSubtasksByEpic(addedEpic_1);
        assertEquals(2, epic1Subtasks.size());
    }

    @Test
    void shouldBeListOfAllTasks() {
        ArrayList<Task> allTasks = taskManager.getAllTasks();
        Task task = taskManager.getTaskById(1);
        assertEquals(2, allTasks.size());
        assertTrue(allTasks.contains(task));
    }

    @Test
    void shouldBeListOfAllSubtasks() {
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        Subtask subtask = taskManager.getSubtaskById(4);
        assertEquals(3, subtasks.size());
        assertTrue(subtasks.contains(subtask));
    }

    @Test
    void shouldBeListOfAllEpics() {
        ArrayList<Epic> epics = taskManager.getAllEpics();
        Epic epic = taskManager.getEpicById(3);
        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic));
    }

    @Test
    void shouldBeRemovedTaskById() {
        taskManager.removeTask(1);
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldBeRemovedSubtaskById() {
        taskManager.removeSubtask(4);
        assertNull(taskManager.getSubtaskById(4));
    }

    @Test
    void shouldBeRemovedEpicById() {
        Epic epic = taskManager.getEpicById(6);
        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpic(epic);
        assertEquals(1, epicSubtasks.size());
        taskManager.removeEpic(6);
        assertNull(taskManager.getEpicById(6));
    }

    @Test
    void shouldBeRemovedAllTasks() {
        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void shouldBeRemovedAllSubtasks() {
        taskManager.removeAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void shouldBeRemovedAllEpics() {
        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void shouldBeAddedNewTask() {
        assertEquals("Купить чай", taskManager.getTaskById(1).getName());
        assertEquals("Вкусный", taskManager.getTaskById(1).getDescription());
    }

    //    проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    void shouldBeAddedNewSubtask() {
        assertEquals("Купить продукты", taskManager.getSubtaskById(4).getName());
        assertEquals("Сделать заказ в Ленте", taskManager.getSubtaskById(4).getDescription());
    }

    @Test
    void shouldBeAddedNewEpic() {
        assertEquals("Дом", taskManager.getEpicById(3).getName());
        assertEquals("Здесь будут задачи по дому", taskManager.getEpicById(3).getDescription());
    }

    @Test
    void shouldBeUpdatedTaskName() {
        Task task = taskManager.getTaskById(1);
        task.setName("Купить кофе");
        Task updatedTask = taskManager.updateTask(task);
        assertEquals("Купить кофе", taskManager.getTaskById(1).getName());
        assertEquals("Купить кофе", updatedTask.getName());
    }

    @Test
    void shouldBeUpdatedSubtask() {
        Subtask subtask = taskManager.getSubtaskById(4);
        subtask.setName("Купить кофе");
        subtask.setTaskState(TaskState.DONE);
        Subtask updatedSubtask = taskManager.updateSubtask(subtask);
        assertEquals("Купить кофе", taskManager.getSubtaskById(4).getName());
        assertEquals("Купить кофе", updatedSubtask.getName());
        assertEquals(TaskState.DONE, updatedSubtask.getTaskState());
    }

    @Test
    void shouldBeUpdatedEpic() {
        Epic epic = taskManager.getEpicById(3);
        epic.setName("Купить кофе");
        Subtask subtask = taskManager.getSubtaskById(4);
        subtask.setTaskState(TaskState.DONE);
        Subtask updatedSubtask = taskManager.updateSubtask(subtask);
        Task updatedEpic = taskManager.updateEpic(epic);
        assertEquals("Купить кофе", taskManager.getEpicById(3).getName());
        assertEquals("Купить кофе", updatedEpic.getName());
        assertEquals(TaskState.IN_PROGRESS, epic.getTaskState());
    }

    //    проверьте, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    public void shouldBePositiveWhenTasksIdIsEquals() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        Task task_2 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        assertEquals(task_1, task_2);
    }

    @Test
    public void shouldBeNegativeWhenTasksIdIsNotEquals() {
        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        Task addedTask_1 = taskManager.createNewTask(task_1);
        Task task_2 = new Task("Посмотреть вебинар", "Запись эфира на capoeiraskills", TaskState.NEW);
        Task addedTask_2 = taskManager.createNewTask(task_2);

        assertNotEquals(addedTask_1, addedTask_2);
    }

    // проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void shouldBeNegativeWhenEpicsIdIsNotEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1);

        Epic epic_2 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_2 = taskManager.createNewEpic(epic_2);

        assertNotEquals(addedEpic_1, addedEpic_2);
    }

    @Test
    public void shouldBePositiveWhenEpicsIdIsEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic epic_2 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);

        assertEquals(epic_1, epic_2);
    }

    @Test
    public void shouldBeNegativeWhenSubtasksIdIsNotEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1);
        Subtask subtask_1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_1 = taskManager.createNewSubtask(subtask_1);
        Subtask subtask_2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_2 = taskManager.createNewSubtask(subtask_2);

        assertNotEquals(addedSubtask_1, addedSubtask_2);
    }

    @Test
    public void shouldBePositiveWhenSubtasksIdIsEquals() {
        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1);
        Subtask subtask_1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, addedEpic_1.getId());
        Subtask subtask_2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, addedEpic_1.getId());

        assertEquals(subtask_1, subtask_2);
    }
}