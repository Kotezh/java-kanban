import tasktracker.model.Epic;
import tasktracker.model.Subtask;
import tasktracker.model.Task;
import tasktracker.model.TaskState;
import tasktracker.service.FileBackedTaskManager;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        File file = new File("files/test.csv");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task("Купить чай", "Вкусный", TaskState.NEW, "01.12.2026 10:00", 30L);
        Task addedTask1 = taskManager.createNewTask(task1);
        Task task2 = new Task("Посмотреть вебинар", "Запись эфира на capoeiraskills", TaskState.NEW, "02.11.2026 12:00", 30L);
        Task addedTask2 = taskManager.createNewTask(task2);

        Epic epic1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic1 = taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, "03.12.2025 13:00", 30L, addedEpic1.getId());
        Subtask addedSubtask1 = taskManager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, "04.12.2025 14:00", 30L, addedEpic1.getId());
        Subtask addedSubtask2 = taskManager.createNewSubtask(subtask2);

        Epic epic2 = new Epic("Работа", "Здесь будут задачи по работе", TaskState.NEW);
        Epic addedEpic2 = taskManager.createNewEpic(epic2);
        Subtask subtask3 = new Subtask("Добить таску", "Исправить валидацию формы и значение в поле Название магазина", TaskState.NEW, "05.01.2025 15:00", 30L, addedEpic2.getId());
        Subtask addedSubtask3 = taskManager.createNewSubtask(subtask3);

        System.out.println("\nСписок задач:");
        ArrayList<Task> allTasks = taskManager.getAllTasks();
        System.out.println(allTasks);
        System.out.println("\nСписок эпиков:");
        ArrayList<Epic> allEpics = taskManager.getAllEpics();
        System.out.println(allEpics);
        System.out.println("\nСписок подзадач:");
        ArrayList<Subtask> allSubtasks = taskManager.getAllSubtasks();
        System.out.println(allSubtasks);
        System.out.println("\nИстория:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("\nСмена статусов:");
        addedTask1.setTaskState(TaskState.IN_PROGRESS);
        Task updatedTask1 = taskManager.updateTask(addedTask1);
        System.out.println(updatedTask1.getTaskState());

        addedTask2.setTaskState(TaskState.DONE);
        Task updatedTask2 = taskManager.updateTask(addedTask2);
        System.out.println(updatedTask2.getTaskState());

        addedSubtask1.setTaskState(TaskState.IN_PROGRESS);
        Subtask updatedSubtask1 = taskManager.updateSubtask(addedSubtask1);
        System.out.println(updatedSubtask1.getTaskState());

        addedSubtask2.setTaskState(TaskState.DONE);
        Subtask updatedSubtask2 = taskManager.updateSubtask(addedSubtask2);
        System.out.println(updatedSubtask2.getTaskState());

        addedSubtask3.setTaskState(TaskState.IN_PROGRESS);
        Subtask updatedSubtask3 = taskManager.updateSubtask(addedSubtask3);
        System.out.println(updatedSubtask3.getTaskState());

        System.out.println("\n Статусы задач:");
        System.out.println(addedTask1.getName() + ": " + addedTask1.getTaskState());
        System.out.println(addedTask2.getName() + ": " + addedTask2.getTaskState());
        System.out.println(addedEpic1.getName() + ": " + addedEpic1.getTaskState());
        System.out.println(addedSubtask1.getName() + ": " + addedSubtask1.getTaskState());
        System.out.println(addedSubtask2.getName() + ": " + addedSubtask2.getTaskState());
        System.out.println(addedEpic2.getName() + ": " + addedEpic2.getTaskState());
        System.out.println(addedSubtask3.getName() + ": " + addedSubtask3.getTaskState());

        System.out.println("\nИзменение задач:");
        addedTask1.setDescription("Вкусный и успокаивающий");
        updatedTask1 = taskManager.updateTask(addedTask1);
        System.out.println("Задача обновлена: " + updatedTask1.getDescription());
        addedSubtask1.setDescription("Купить продукты на неделю");
        updatedSubtask1 = taskManager.updateSubtask(addedSubtask1);
        System.out.println("Подзадача обновлена: " + updatedSubtask1.getDescription());
        addedEpic1.setName("Дом и хобби");
        Epic updatedEpic1 = taskManager.updateEpic(addedEpic1);
        System.out.println("Эпик обновлен: " + updatedEpic1.getName());

        System.out.println("\nПоиск подзадач эпика:");
        ArrayList<Subtask> subtasksByEpic1 = taskManager.getSubtasksByEpic(updatedEpic1);
        System.out.printf("Подзадачи эпика 1: %s\n", subtasksByEpic1);

        System.out.println("\nУдаление задач по id:");
        taskManager.removeTask(updatedTask1.getId());
        System.out.printf("Задача '%s' удалена\n", updatedTask1.getName());
        taskManager.removeSubtask(updatedSubtask1.getId());
        System.out.printf("Подзадача '%s' удалена\n", updatedSubtask1.getName());

        taskManager.removeEpic(addedEpic2.getId());
        System.out.printf("Эпик '%s' удален\n", addedEpic2.getName());

        System.out.println("\nСписок задач:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("\nСписок эпиков:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("\nСписок подзадач:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("\nИстория:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("\n Отсортированный список задач:");
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("\nУдаление всех задач:");
        taskManager.removeAllTasks();
        if (taskManager.getAllTasks().isEmpty()) {
            System.out.println("Все задачи удалены");
        }
        taskManager.removeAllSubtasks();
        if (taskManager.getAllSubtasks().isEmpty()) {
            System.out.println("Все подзадачи удалены");
        }
        taskManager.removeAllEpics();
        if (taskManager.getAllEpics().isEmpty()) {
            System.out.println("Все эпики удалены");
        }
    }
}
