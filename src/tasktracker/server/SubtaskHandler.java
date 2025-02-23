package tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.DateTimeException;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Subtask;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("subtasks")) {
            try {
                String response = "";
                if (pathParts.length == 2) {
                    response = gson.toJson(taskManager.getAllSubtasks());
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    response = gson.toJson(taskManager.getSubtaskById(taskId));
                }
                sendSuccess(httpExchange, response);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Подзадача не найдена");
            }
        }
    }

    @Override
    public void handlePost(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("subtasks")) {
            try {
                InputStream inputStream = httpExchange.getRequestBody();
                String subtask = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Subtask subtaskObject = gson.fromJson(subtask, Subtask.class);
                if (pathParts.length == 2) {
                    taskManager.createNewSubtask(subtaskObject);
                    sendSuccessUpdate(httpExchange, "Создана новая подзадача");
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    if (taskId != -1) {
                        taskManager.updateSubtask(subtaskObject);
                        sendSuccessUpdate(httpExchange, "Подзадача успешно обновлена");
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Подзадача не найдена");
            } catch (DateTimeException e) {
                sendHasInteractions(httpExchange, "Подзадача пересекается с другой задачей");
            }
        }
    }

    @Override
    public void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("subtasks")) {
            try {
                if (pathParts.length == 2) {
                    taskManager.removeAllSubtasks();
                    sendSuccess(httpExchange, "Все подзадачи удалены");
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    taskManager.removeSubtask(taskId);
                    sendSuccess(httpExchange, "Подзадача удалена");
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Подзадача не найдена");
            }
        }
    }
}
