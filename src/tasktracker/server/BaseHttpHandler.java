package tasktracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
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
}