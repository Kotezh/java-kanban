import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Task;
import tasktracker.model.TaskState;
import tasktracker.service.Managers;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasktracker.model.Task.dateTimeFormatter;

class HttpTaskTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = HttpTaskServer.getGson();
    private static final String URL = "http://localhost:8080/tasks";

    @BeforeEach
    void beforeEach() throws IOException {
//        taskManager.removeAllEpics();
//        taskManager.removeAllTasks();
//        taskManager.removeAllSubtasks();
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    void afterEach() {
        taskManager.removeAllEpics();
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        httpTaskServer.stop();
    }

    @Test
    public void shouldBeTaskCreated() throws IOException, InterruptedException, NotFoundException {
        Task task1 = new Task("Задача 1", "Задача 1", TaskState.NEW, LocalDateTime.parse("01.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L));

        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskJson)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldBeTaskUpdated() throws IOException, InterruptedException, NotFoundException {
        Task task = new Task("Задача 2", "Задача 2", TaskState.NEW, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L));
        Task updatedTask = new Task("Задача 1", "Задача 1", TaskState.DONE, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L));
        taskManager.createNewTask(task);
        String taskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskJson)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
//        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TaskState.DONE, taskManager.getTaskById(0).getTaskState(), "Некорректный статус задачи");
    }
}