package tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.DateTimeException;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("tasks")) {
            try {
                String response = "";
                if (pathParts.length == 2) {
                    response = gson.toJson(taskManager.getAllTasks());
                    sendSuccess(httpExchange, response);
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    response = gson.toJson(taskManager.getTaskById(taskId));
                    sendSuccess(httpExchange, response);
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Задача не найдена");
            }
        }
    }

    @Override
    public void handlePost(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("tasks")) {
            try {
                InputStream inputStream = httpExchange.getRequestBody();
                String task = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task taskObject = gson.fromJson(task, Task.class);
                if (pathParts.length == 2) {
                    taskManager.createNewTask(taskObject);
                    sendSuccessUpdate(httpExchange, "Создана новая задача");
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    if (taskId != -1) {
                        taskManager.updateTask(taskObject);
                        sendSuccessUpdate(httpExchange, "Задача успешно обновлена");
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Задача не найдена");
            } catch (DateTimeException e) {
                sendHasInteractions(httpExchange, "Задача пересекается с другой задачей");
            }
        }
    }

    @Override
    public void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("tasks")) {
            try {
                if (pathParts.length == 2) {
                    taskManager.removeAllTasks();
                    sendSuccess(httpExchange, "Все задачи удалены");
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    taskManager.removeTask(taskId);
                    sendSuccess(httpExchange, "Задача удалена");
                }

            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Задача не найдена");
            }
        }
    }
}
