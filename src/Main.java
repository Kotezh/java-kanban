import taskTracker.model.Epic;
import taskTracker.model.Subtask;
import taskTracker.model.Task;
import taskTracker.model.TaskState;
import taskTracker.service.InMemoryTaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task_1 = new Task("Купить чай", "Вкусный", TaskState.NEW);
        Task addedTask_1 = taskManager.createNewTask(task_1);
        Task task_2 = new Task("Посмотреть вебинар", "Запись эфира на capoeiraskills", TaskState.NEW);
        Task addedTask_2 = taskManager.createNewTask(task_2);

        Epic epic_1 = new Epic("Дом", "Здесь будут задачи по дому", TaskState.NEW);
        Epic addedEpic_1 = taskManager.createNewEpic(epic_1);
        Subtask subtask_1 = new Subtask("Купить продукты", "Сделать заказ в Ленте", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_1 = taskManager.createNewSubtask(subtask_1);
        Subtask subtask_2 = new Subtask("Приготовить суши-торт", "решить запеченный или нет", TaskState.NEW, addedEpic_1.getId());
        Subtask addedSubtask_2 = taskManager.createNewSubtask(subtask_2);

        Epic epic_2 = new Epic("Работа", "Здесь будут задачи по работе", TaskState.NEW);
        Epic addedEpic_2 = taskManager.createNewEpic(epic_2);
        Subtask subtask_3 = new Subtask("Добить таску", "Исправить валидацию формы и значение в поле Название магазина", TaskState.NEW, addedEpic_2.getId());
        Subtask addedSubtask_3 = taskManager.createNewSubtask(subtask_3);

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
        addedTask_1.setTaskState(TaskState.IN_PROGRESS);
        Task updatedTask_1 = taskManager.updateTask(addedTask_1);
        System.out.println(updatedTask_1.getTaskState());

        addedTask_2.setTaskState(TaskState.DONE);
        Task updatedTask_2 = taskManager.updateTask(addedTask_2);
        System.out.println(updatedTask_2.getTaskState());

        addedSubtask_1.setTaskState(TaskState.IN_PROGRESS);
        Subtask updatedSubtask_1 = taskManager.updateSubtask(addedSubtask_1);
        System.out.println(updatedSubtask_1.getTaskState());

        addedSubtask_2.setTaskState(TaskState.DONE);
        Subtask updatedSubtask_2 = taskManager.updateSubtask(addedSubtask_2);
        System.out.println(updatedSubtask_2.getTaskState());

        addedSubtask_3.setTaskState(TaskState.IN_PROGRESS);
        Subtask updatedSubtask_3 = taskManager.updateSubtask(addedSubtask_3);
        System.out.println(updatedSubtask_3.getTaskState());

        System.out.println("\n Статусы задач:");
        System.out.println(addedTask_1.getName() + ": " + addedTask_1.getTaskState());
        System.out.println(addedTask_2.getName() + ": " + addedTask_2.getTaskState());
        System.out.println(addedEpic_1.getName() + ": " + addedEpic_1.getTaskState());
        System.out.println(addedSubtask_1.getName() + ": " + addedSubtask_1.getTaskState());
        System.out.println(addedSubtask_2.getName() + ": " + addedSubtask_2.getTaskState());
        System.out.println(addedEpic_2.getName() + ": " + addedEpic_2.getTaskState());
        System.out.println(addedSubtask_3.getName() + ": " + addedSubtask_3.getTaskState());

        System.out.println("\nИзменение задач:");
        addedTask_1.setDescription("Вкусный и успокаивающий");
        updatedTask_1 = taskManager.updateTask(addedTask_1);
        System.out.println("Задача обновлена: " + updatedTask_1.getDescription());
        addedSubtask_1.setDescription("Купить продукты на неделю");
        updatedSubtask_1 = taskManager.updateSubtask(addedSubtask_1);
        System.out.println("Подзадача обновлена: " + updatedSubtask_1.getDescription());
        addedEpic_1.setName("Дом и хобби");
        Epic updatedEpic_1 = taskManager.updateEpic(addedEpic_1);
        System.out.println("Эпик обновлен: " + updatedEpic_1.getName());

        System.out.println("\nПоиск подзадач эпика:");
        ArrayList<Subtask> subtasksByEpic_1 = taskManager.getSubtasksByEpic(updatedEpic_1);
        System.out.printf("Подзадачи эпика 1: %s\n", subtasksByEpic_1);

        System.out.println("\nУдаление задач по id:");
        taskManager.removeTask(updatedTask_1.getId());
        System.out.printf("Задача '%s' удалена\n", updatedTask_1.getName());
        taskManager.removeSubtask(updatedSubtask_1.getId());
        System.out.printf("Подзадача '%s' удалена\n", updatedSubtask_1.getName());

        taskManager.removeEpic(addedEpic_2.getId());
        System.out.printf("Эпик '%s' удален\n", addedEpic_2.getName());

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
