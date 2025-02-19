package tasktracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.DateTimeException;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Subtask;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();

            switch (requestMethod) {
                case "GET": {
                    handleGet(exchange);
                    break;
                }
                case "POST": {
                    handlePost(exchange);
                    break;
                }
                case "DELETE": {
                    handleDelete(exchange);
                    break;
                }
                default: {
                    sendNotFound(exchange, "Такого эндпоинта не существует");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("subtasks")) {
            try {
                String response = "";
                if (pathParts.length == 2) {
                    response = gson.toJson(taskManager.getAllSubtasks());
                }
                if (pathParts.length == 4) {
                    int taskId = Integer.parseInt(pathParts[3]);
                    response = gson.toJson(taskManager.getSubtaskById(taskId));
                }
                sendSuccess(httpExchange, response);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Подзадача не найдена");
            }
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
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
                if (pathParts.length == 4) {
//                int taskId = Integer.parseInt(pathParts[3]);
                    taskManager.updateSubtask(subtaskObject);
                    sendSuccessUpdate(httpExchange, "Подзадача успешно обновлена");
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Подзадача не найдена");
            } catch (DateTimeException e) {
                sendHasInteractions(httpExchange, "Подзадача пересекается с другой задачей");
            }
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("subtasks")) {
            try {
                if (pathParts.length == 2) {
                    taskManager.removeAllSubtasks();
                    sendSuccess(httpExchange, "Все подзадачи удалены");
                }
                if (pathParts.length == 4) {
                    int taskId = Integer.parseInt(pathParts[3]);
                    taskManager.removeSubtask(taskId);
                    sendSuccess(httpExchange, "Подзадача удалена");
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Подзадача не найдена");
            }
        }
    }
}
