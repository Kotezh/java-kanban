import com.sun.net.httpserver.HttpServer;
import tasktracker.server.SubtaskHandler;
import tasktracker.server.TaskHandler;
import tasktracker.server.EpicHandler;
import tasktracker.server.HistoryHandler;
import tasktracker.server.PrioritizedHandler;
import tasktracker.service.Managers;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
            httpServer.createContext("/epics", new EpicHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

        } catch (IOException exception) {
            throw new RuntimeException("Сервер не создан на порту " + PORT);
        }
    }

    public void start() {
        httpServer.start();
        System.out.printf("Сервер запущен на %s порту ", PORT);
    }

    public void stop() {
        httpServer.stop(1);
        System.out.printf("Сервер остановлен на %s порту ", PORT);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        taskServer.stop();
    }
}
