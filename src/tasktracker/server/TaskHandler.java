package tasktracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.DateTimeException;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Task;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TaskHandler extends BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
        System.out.println(Arrays.toString(pathParts));
        if (pathParts[1].equals("tasks")) {
            try {
                String response = "";
                if (pathParts.length == 2) {
                    response = gson.toJson(taskManager.getAllTasks());
                }
                if (pathParts.length == 4) {
                    int taskId = Integer.parseInt(pathParts[3]);
                    response = gson.toJson(taskManager.getTaskById(taskId));
                }
                sendSuccess(httpExchange, response);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Задача не найдена");
            }
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        System.out.println(Arrays.toString(pathParts));
        if (pathParts[1].equals("tasks")) {
            try {
                InputStream inputStream = httpExchange.getRequestBody();
                String task = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task taskObject = gson.fromJson(task, Task.class);
                if (pathParts.length == 2) {
                    taskManager.createNewTask(taskObject);
                    sendSuccessUpdate(httpExchange, "Создана новая задача");
                }
                if (pathParts.length == 4) {
//                int taskId = Integer.parseInt(pathParts[1]);
                    taskManager.updateTask(taskObject);
                    sendSuccessUpdate(httpExchange, "Задача успешно обновлена");
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Задача не найдена");
            } catch (DateTimeException e) {
                sendHasInteractions(httpExchange, "Задача пересекается с другой задачей");
            }
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        System.out.println(Arrays.toString(pathParts));
        if (pathParts[1].equals("tasks")) {
            try {
                if (pathParts.length == 2) {
                    taskManager.removeAllTasks();
                    sendSuccess(httpExchange, "Все задачи удалены");
                }
                if (pathParts.length == 4) {
                    int taskId = Integer.parseInt(pathParts[3]);
                    taskManager.removeTask(taskId);
                    sendSuccess(httpExchange, "Задача удалена");
                }

            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Задача не найдена");
            }
        }
    }
}
