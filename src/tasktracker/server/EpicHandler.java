package tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.DateTimeException;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Epic;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("epics")) {
            try {
                String response = "";
                if (pathParts.length == 2) {
                    response = gson.toJson(taskManager.getAllEpics());
                    sendSuccess(httpExchange, response);
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    response = gson.toJson(taskManager.getEpicById(taskId));
                    sendSuccess(httpExchange, response);
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Эпик не найден");
            }
        }
    }

    @Override
    public void handlePost(HttpExchange httpExchange) throws IOException {
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
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
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

    @Override
    public void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts[1].equals("epics")) {
            try {
                if (pathParts.length == 2) {
                    taskManager.removeAllEpics();
                    sendSuccess(httpExchange, "Все эпики удалены");
                }
                if (pathParts.length == 3) {
                    int taskId = Integer.parseInt(pathParts[2]);
                    taskManager.removeEpic(taskId);
                    sendSuccess(httpExchange, "Эпик удален");
                }

            } catch (NotFoundException e) {
                sendNotFound(httpExchange, "Эпик не найден");
            }
        }
    }
}
