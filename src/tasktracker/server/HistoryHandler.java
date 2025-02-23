package tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import tasktracker.exceptions.NotFoundException;
import tasktracker.service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod();

            if (requestMethod.equals("GET")) {
                handleGet(exchange);
            } else {
                sendNotFound(exchange, "Такого эндпоинта не существует");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleGet(HttpExchange httpExchange) throws IOException {
        try {
            String history = gson.toJson(taskManager.getHistory());
            sendSuccess(httpExchange, history);

        } catch (NotFoundException e) {
            sendNotFound(httpExchange, "История не найдена");
        }
    }
}
