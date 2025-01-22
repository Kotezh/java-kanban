package tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.DateTimeException;
import tasktracker.model.Epic;
import tasktracker.model.Subtask;
import tasktracker.model.Task;
import tasktracker.model.TaskState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task addedTask1;
    protected Task addedTask2;
    protected Epic addedEpic1;
    protected Subtask addedSubtask1;
    protected Subtask addedSubtask2;
    protected Subtask addedSubtask3;
    protected Epic addedEpic2;
    protected Subtask addedSubtask4;

    @BeforeEach
    void beforeEach() {
        Task task1 = new Task("Задача 1", "Задача 1", TaskState.NEW, "01.01.2025 11:00", 30L);
        addedTask1 = taskManager.createNewTask(task1); // id=1
        Task task2 = new Task("Задача 2", "Задача 2", TaskState.NEW, "02.01.2025 11:00", 40L);
        addedTask2 = taskManager.createNewTask(task2); // id=2

        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи", TaskState.NEW);
        addedEpic1 = taskManager.createNewEpic(epic1); // id=3
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, "03.01.2025 11:00", 50L, addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=4
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2, Эпик 1", TaskState.NEW, "04.01.2025 11:00", 60L, addedEpic1.getId());
        addedSubtask2 = taskManager.createNewSubtask(subtask2); // id=5
        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3, Эпик 1", TaskState.NEW, "05.01.2025 11:00", 70L, addedEpic1.getId());
        addedSubtask3 = taskManager.createNewSubtask(subtask3); // id=6

        Epic epic2 = new Epic("Эпик 2", "Эпик 2, 1 подзадача", TaskState.NEW);
        addedEpic2 = taskManager.createNewEpic(epic2); // id=7
        Subtask subtask4 = new Subtask("Подзадача 4", "Подзадача 4, Эпик 2", TaskState.NEW, "06.01.2025 11:00", 50L, addedEpic2.getId());
        addedSubtask4 = taskManager.createNewSubtask(subtask4); // id=8
    }

    @Test
    void shouldBeTaskWithId1() {
        Task task = taskManager.getTaskById(1);
        assertNotNull(task, "Задача отсутствует");
        assertEquals(1, task.getId());
        assertEquals("Задача 1", task.getName());
    }

    @Test
    void shouldBeSubtaskWithId4() {
        Subtask subtask1 = taskManager.getSubtaskById(4);
        assertNotNull(subtask1, "Задача отсутствует");
        assertEquals(4, subtask1.getId());
        assertEquals("Подзадача 1", subtask1.getName());
    }

    @Test
    void shouldBeEpicWithId3() {
        Epic epic1 = taskManager.getEpicById(3);
        assertNotNull(epic1, "Задача отсутствует");
        assertEquals(3, epic1.getId());
        assertEquals("Эпик 1", epic1.getName());
    }

    @Test
    void shouldBeSubtasksByEpicId3() {
        Epic addedEpic1 = taskManager.getEpicById(3);
        ArrayList<Subtask> epic1Subtasks = taskManager.getSubtasksByEpic(addedEpic1);
        assertEquals(3, epic1Subtasks.size());
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
        assertEquals(4, subtasks.size());
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
        Epic epic = taskManager.getEpicById(7);
        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpic(epic);
        assertEquals(1, epicSubtasks.size());
        taskManager.removeEpic(7);
        assertNull(taskManager.getEpicById(7));
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
        assertEquals("Задача 1", taskManager.getTaskById(1).getName());
        assertEquals("Задача 1", taskManager.getTaskById(1).getDescription());
    }

    @Test
    void shouldBeAddedNewSubtask() {
        assertEquals("Подзадача 1", taskManager.getSubtaskById(4).getName());
        assertEquals("Подзадача 1, Эпик 1", taskManager.getSubtaskById(4).getDescription());
    }

    @Test
    void shouldBeAddedNewEpic() {
        assertEquals("Эпик 1", taskManager.getEpicById(3).getName());
        assertEquals("Эпик 1, 3 подзадачи", taskManager.getEpicById(3).getDescription());
    }

    @Test
    void shouldBeUpdatedTaskName() {
        Task task = taskManager.getTaskById(1);
        task.setName("Задача 1.1");
        Task updatedTask = taskManager.updateTask(task);
        assertEquals("Задача 1.1", taskManager.getTaskById(1).getName());
        assertEquals("Задача 1.1", updatedTask.getName());
    }

    @Test
    void shouldBeUpdatedSubtask() {
        Subtask subtask = taskManager.getSubtaskById(4);
        subtask.setName("Подзадача 1.1");
        subtask.setTaskState(TaskState.DONE);
        Subtask updatedSubtask = taskManager.updateSubtask(subtask);
        assertEquals("Подзадача 1.1", taskManager.getSubtaskById(4).getName());
        assertEquals("Подзадача 1.1", updatedSubtask.getName());
        assertEquals(TaskState.DONE, taskManager.getSubtaskById(4).getTaskState());
    }

    @Test
    void shouldBeUpdatedEpic() {
        Epic epic = taskManager.getEpicById(7);
        epic.setName("Эпик 1.1");
        Subtask subtask = taskManager.getSubtaskById(8);
        subtask.setTaskState(TaskState.DONE);
        Subtask updatedSubtask = taskManager.updateSubtask(subtask);
        assertEquals("Эпик 1.1", taskManager.getEpicById(7).getName());
        assertEquals("Эпик 1.1", epic.getName());
        assertEquals(TaskState.DONE, taskManager.getEpicById(7).getTaskState());
    }

    @Test
    public void shouldBePositiveWhenTasksIdIsEquals() {
        Task task1 = new Task("Задача 1.0", "Задача 1.0", TaskState.NEW);
        Task task2 = new Task("Задача 1.0", "Задача 1.0", TaskState.NEW);
        assertEquals(task1, task2);
    }

    @Test
    public void shouldBeNegativeWhenTasksIdIsNotEquals() {
        assertNotEquals(addedTask1, addedTask2);
    }

    // проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void shouldBeNegativeWhenEpicsIdIsNotEquals() {
        assertNotEquals(addedEpic1, addedEpic2);
    }

    @Test
    public void shouldBePositiveWhenEpicsIdIsEquals() {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1", TaskState.NEW);
        Epic epic2 = new Epic("Эпик 1", "Эпик 1", TaskState.NEW);

        assertEquals(epic1, epic2);
    }

    @Test
    public void shouldBeNegativeWhenSubtasksIdIsNotEquals() {
        assertNotEquals(addedSubtask1, addedSubtask2);
    }

    @Test
    public void shouldBePositiveWhenSubtasksIdIsEquals() {
        Subtask subtask1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, addedEpic1.getId());
        Subtask subtask2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, addedEpic1.getId());

        assertEquals(subtask1, subtask2);
    }

    @Test
    public void shouldBeCreatedNewSubtasks() {
        assertTrue(taskManager.getAllSubtasks().stream().allMatch(subtask -> subtask.getTaskState() == TaskState.NEW));
        assertTrue(taskManager.getEpicById(3).getTaskState() == TaskState.NEW);
    }

    @Test
    public void shouldBeOneSubtaskInProgress() {
        Subtask subtask = taskManager.getSubtaskById(4);
        subtask.setTaskState(TaskState.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertTrue(taskManager.getSubtaskById(4).getTaskState() == TaskState.IN_PROGRESS);
    }

    @Test
    public void shouldBeOneSubtaskDone() {
        Subtask subtask = taskManager.getSubtaskById(5);
        subtask.setTaskState(TaskState.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateEpic(taskManager.getEpicById(3));
        assertTrue(taskManager.getSubtaskById(5).getTaskState() == TaskState.DONE);
    }

    @Test
    public void shouldBeAllSubtasksDone() {
        taskManager.getAllSubtasks().forEach(subtask -> {
            subtask.setTaskState(TaskState.DONE);
            taskManager.updateSubtask(subtask);
            taskManager.updateEpic(taskManager.getEpicById(3));
        });

        assertTrue(taskManager.getAllSubtasks().stream().allMatch(subtask -> subtask.getTaskState()  == TaskState.DONE));
    }

    @Test
    public void shouldNotCrossTime() {
        assertThrows(DateTimeException.class, () -> {
            addedTask2.setStartTime(LocalDateTime.parse("01.01.2025 11:00", Task.dateTimeFormatter));
            taskManager.updateTask(addedTask2);
        });
        assertDoesNotThrow(() -> {
            addedTask2.setStartTime(LocalDateTime.parse("01.02.2025 11:00", Task.dateTimeFormatter));
            taskManager.updateTask(addedTask2);
        });
    }
}