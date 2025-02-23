import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Epic;
import tasktracker.model.Subtask;
import tasktracker.model.TaskState;
import tasktracker.server.BaseHttpHandler;
import tokens.SubtaskTypeToken;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpSubtaskTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = BaseHttpHandler.getGson();
    private static final String URL = "http://localhost:8080/subtasks";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected Epic addedEpic1;
    protected Subtask addedSubtask1;
    protected Subtask addedSubtask2;
    protected Subtask addedSubtask3;

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
    public void shouldBeGetAllSubtasks() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=2
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2, Эпик 1", TaskState.NEW, LocalDateTime.parse("04.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask2 = taskManager.createNewSubtask(subtask2); // id=3
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(response.body(), new SubtaskTypeToken().getType());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Некорректное количество подзадач");
        assertEquals(taskManager.getAllSubtasks(), subtasks, "Некорректное количество подзадач");
    }

    @Test
    public void shouldBeGetSubtaskById() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=2
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2, Эпик 1", TaskState.NEW, LocalDateTime.parse("04.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask2 = taskManager.createNewSubtask(subtask2); // id=3
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(response.body(), new TypeToken<Subtask>() {
        }.getType());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        assertNotNull(subtask, "Подзадача не возвращается");
        assertEquals(taskManager.getSubtaskById(3), subtask, "Некорректная подзадача");
    }

    @Test
    public void shouldBeSubtaskCreated() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=2
        String taskJson = gson.toJson(addedSubtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void shouldBeSubtaskUpdated() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=2
        addedSubtask1.setTaskState(TaskState.DONE);
        String taskJson = gson.toJson(addedSubtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals(TaskState.DONE, taskManager.getSubtaskById(2).getTaskState(), "Некорректный статус подзадачи");
    }

    @Test
    public void shouldBeCrossedSubtasks() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=2
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        String taskJson = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Статусы ответа не совпадает");
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Подзадача 1", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldBeSubtaskDeleted() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=2
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2, Эпик 1", TaskState.NEW, LocalDateTime.parse("04.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask2 = taskManager.createNewSubtask(subtask2); // id=3
        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3, Эпик 1", TaskState.NEW, LocalDateTime.parse("05.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask3 = taskManager.createNewSubtask(subtask3); // id=4

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertEquals(3, subtasksFromManager.size(), "Некорректное количество задач");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        List<Subtask> subtasksAfterFromManager = taskManager.getAllSubtasks();
        assertEquals(2, subtasksAfterFromManager.size(), "Некорректное количество задач");
    }
}