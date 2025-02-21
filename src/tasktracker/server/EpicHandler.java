package tasktracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.DateTimeException;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Epic;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) {
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
        if (pathParts[1].equals("epics")) {
            try {
                String response = "";
                if (pathParts.length == 2) {
                    response = gson.toJson(taskManager.getAllEpics());
                }
                if (pathParts.length == 4) {
                    int taskId = Integer.parseInt(pathParts[3]);
                    response = gson.toJson(taskManager.getEpicById(taskId));
                }
                sendSuccess(httpExchange, response);

            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Эпик не найден");
            }
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("epics")) {
            try {
                InputStream inputStream = httpExchange.getRequestBody();
                String epic = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Epic epicObject = gson.fromJson(epic, Epic.class);
                if (pathParts.length == 2) {
                    taskManager.createNewEpic(epicObject);
                    sendSuccessUpdate(httpExchange, "Создан новый эпик");
                }
                if (pathParts.length == 4) {
                    int taskId = Integer.parseInt(pathParts[3]);
                    if (taskId != -1) {
                        taskManager.updateEpic(epicObject);
                        sendSuccessUpdate(httpExchange, "Эпик успешно обновлен");
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Эпик не найден");
            } catch (DateTimeException e) {
                sendHasInteractions(httpExchange, "Эпик пересекается с другой задачей");
            }
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("epics")) {
            try {
                if (pathParts.length == 2) {
                    taskManager.removeAllEpics();
                    sendSuccess(httpExchange, "Все эпики удалены");
                }
                if (pathParts.length == 4) {
                    int taskId = Integer.parseInt(pathParts[3]);
                    taskManager.removeEpic(taskId);
                    sendSuccess(httpExchange, "Эпик удален");
                }

            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Эпик не найден");
            }
        }
    }
}
