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

public class HistoryHandler extends BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
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

    private void handleGet(HttpExchange httpExchange) throws IOException {
        try {
            String history = gson.toJson(taskManager.getHistory());
            sendSuccess(httpExchange, history);

        } catch (NotFoundException e) {
            sendNotFound(httpExchange, "История не найдена");
        }
    }
}
