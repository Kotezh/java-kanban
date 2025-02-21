import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Task;
import tasktracker.model.TaskState;
import tasktracker.server.TaskTypeToken;
import tasktracker.service.Managers;
import tasktracker.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = HttpTaskServer.getGson();
    private static final String URL = "http://localhost:8080/tasks";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    public void shouldBeGetAllTasks() throws IOException, InterruptedException, NotFoundException {
        taskManager.createNewTask(new Task("Задача 2", "Задача 2", TaskState.NEW, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L)));
        taskManager.createNewTask(new Task("Задача 1", "Задача 1", TaskState.DONE, LocalDateTime.parse("01.01.2027 11:00", dateTimeFormatter), Duration.ofMinutes(30L)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Некорректное количество задач");
        assertEquals(taskManager.getAllTasks(), tasks, "Некорректное количество задач");
    }

    @Test
    public void shouldBeGetTaskById() throws IOException, InterruptedException, NotFoundException {
        taskManager.createNewTask(new Task("Задача 2", "Задача 2", TaskState.NEW, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L)));
        taskManager.createNewTask(new Task("Задача 1", "Задача 1", TaskState.DONE, LocalDateTime.parse("01.01.2027 11:00", dateTimeFormatter), Duration.ofMinutes(30L)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        Task task = gson.fromJson(response.body(), new TypeToken<Task>() {
        }.getType());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        assertNotNull(task, "Задача не возвращается");
        assertEquals(taskManager.getTaskById(1), task, "Некорректная задача");
    }

    @Test
    public void shouldBeTaskCreated() throws IOException, InterruptedException, NotFoundException {
        Task task1 = new Task("Задача 1", "Задача 1", TaskState.NEW, LocalDateTime.parse("01.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L));
        String taskJson = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldBeTaskUpdated() throws IOException, InterruptedException, NotFoundException {
        Task task = new Task("Задача 1", "Задача 1", TaskState.DONE, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L));
        taskManager.createNewTask(task);
        task.setTaskState(TaskState.DONE);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TaskState.DONE, taskManager.getTaskById(1).getTaskState(), "Некорректный статус задачи");
    }

    @Test
    public void shouldBeCrossedTasks() throws IOException, InterruptedException, NotFoundException {
        taskManager.createNewTask(new Task("Задача 2", "Задача 2", TaskState.NEW, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L)));
        Task task1 = new Task("Задача 1", "Задача 1", TaskState.NEW, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L));
        String taskJson = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Статусы ответа не совпадает");
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldBeTaskDeleted() throws IOException, InterruptedException, NotFoundException {
        taskManager.createNewTask(new Task("Задача 2", "Задача 2", TaskState.NEW, LocalDateTime.parse("01.01.2026 11:00", dateTimeFormatter), Duration.ofMinutes(30L)));
        taskManager.createNewTask(new Task("Задача 1", "Задача 1", TaskState.DONE, LocalDateTime.parse("01.01.2027 11:00", dateTimeFormatter), Duration.ofMinutes(30L)));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        List<Task> tasksAfterDeleteFromManager = taskManager.getAllTasks();
        assertEquals(1, tasksAfterDeleteFromManager.size(), "Некорректное количество задач");
    }
}