package tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.adapters.DurationAdapter;
import tasktracker.adapters.LocalDateTimeAdapter;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = BaseHttpHandler.getGson();
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

    public void handleGet(HttpExchange httpExchange) throws IOException {
        sendNotAllowed(httpExchange);
    }

    public void handlePost(HttpExchange httpExchange) throws IOException {
        sendNotAllowed(httpExchange);
    }

    public void handleDelete(HttpExchange httpExchange) throws IOException {
        sendNotAllowed(httpExchange);
    }

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {

        byte[] response = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
        h.close();
    }

    protected void sendSuccess(HttpExchange h, String text) throws IOException {
        sendText(h, text, 200);
    }

    protected void sendSuccessUpdate(HttpExchange h, String text) throws IOException {
        sendText(h, text, 201);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendText(h, text, 404);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendText(h, text, 406);
    }

    protected void sendNotAllowed(HttpExchange h) throws IOException {
        sendText(h, "Доступ не разрешен", 405);
    }
}