import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Epic;
import tasktracker.model.TaskState;
import tasktracker.server.EpicTypeToken;
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

class HttpEpicTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = HttpTaskServer.getGson();
    private static final String URL = "http://localhost:8080/epics";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected Epic addedEpic1;
    protected Epic addedEpic2;

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
    public void shouldBeGetAllEpics() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        epic1.setStartTime(LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter));
        epic1.setDuration(Duration.ofMinutes(30L));
        epic1.setEndTime(epic1.getStartTime().plus(epic1.getDuration()));
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Epic epic2 = new Epic("Эпик 2", "Эпик 2, 1 подзадача");
        epic2.setStartTime(LocalDateTime.parse("03.02.2025 11:00", dateTimeFormatter));
        epic2.setDuration(Duration.ofMinutes(30L));
        epic2.setEndTime(epic2.getStartTime().plus(epic2.getDuration()));
        addedEpic2 = taskManager.createNewEpic(epic2); // id=2
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(response.body(), new EpicTypeToken().getType());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(2, epics.size(), "Некорректное количество эпиков");
        assertEquals(taskManager.getAllEpics(), epics, "Некорректные эпики");
    }

    @Test
    public void shouldBeGetEpicById() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        epic1.setStartTime(LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter));
        epic1.setDuration(Duration.ofMinutes(30L));
        epic1.setEndTime(epic1.getStartTime().plus(epic1.getDuration()));
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Epic epic2 = new Epic("Эпик 2", "Эпик 2, 1 подзадача");
        epic2.setStartTime(LocalDateTime.parse("03.02.2025 11:00", dateTimeFormatter));
        epic2.setDuration(Duration.ofMinutes(30L));
        epic2.setEndTime(epic2.getStartTime().plus(epic2.getDuration()));
        addedEpic2 = taskManager.createNewEpic(epic2); // id=2
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(response.body(), new TypeToken<Epic>() {
        }.getType());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        assertNotNull(epic, "Эпики не возвращается");
        assertEquals(taskManager.getEpicById(2), epic, "Некорректный эпик");
    }

    @Test
    public void shouldBeEpicCreated() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        epic1.setStartTime(LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter));
        epic1.setDuration(Duration.ofMinutes(30L));
        epic1.setEndTime(epic1.getStartTime().plus(epic1.getDuration()));
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        String taskJson = gson.toJson(addedEpic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");
        List<Epic> epicsFromManager = taskManager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик 1", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void shouldBeEpicUpdated() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        epic1.setStartTime(LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter));
        epic1.setDuration(Duration.ofMinutes(30L));
        epic1.setEndTime(epic1.getStartTime().plus(epic1.getDuration()));
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        addedEpic1.setTaskState(TaskState.DONE);
        String taskJson = gson.toJson(addedEpic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статусы ответа не совпадает");
        List<Epic> epicsFromManager = taskManager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals(TaskState.DONE, taskManager.getEpicById(1).getTaskState(), "Некорректный статус эпика");
    }

    @Test
    public void shouldBeEpicDeleted() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=1
        Epic epic2 = new Epic("Эпик 2", "Эпик 2, 1 подзадача");
        addedEpic2 = taskManager.createNewEpic(epic2); // id=2
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        List<Epic> epicsFromManager = taskManager.getAllEpics();
        assertEquals(2, epicsFromManager.size(), "Некорректное количество эпиков");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        List<Epic> epicsAfterDeleteFromManager = taskManager.getAllEpics();
        assertEquals(1, epicsAfterDeleteFromManager.size(), "Некорректное количество эпиков");
    }
}