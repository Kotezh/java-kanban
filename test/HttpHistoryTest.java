import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.NotFoundException;
import tasktracker.model.Epic;
import tasktracker.model.Subtask;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpHistoryTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = HttpTaskServer.getGson();
    private static final String URL = "http://localhost:8080/history";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected Task addedTask1;
    protected Task addedTask2;
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
    public void shouldBeGetHistory() throws IOException, InterruptedException, NotFoundException {
        Task task1 = new Task("Задача 1", "Задача 1", TaskState.NEW, LocalDateTime.parse("01.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L));
        addedTask1 = taskManager.createNewTask(task1); // id=1
        Task task2 = new Task("Задача 2", "Задача 2", TaskState.NEW, LocalDateTime.parse("02.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L));
        addedTask2 = taskManager.createNewTask(task2); // id=2
        Epic epic1 = new Epic("Эпик 1", "Эпик 1, 3 подзадачи");
        addedEpic1 = taskManager.createNewEpic(epic1); // id=3
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1, Эпик 1", TaskState.NEW, LocalDateTime.parse("03.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask1 = taskManager.createNewSubtask(subtask1); // id=4
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2, Эпик 1", TaskState.NEW, LocalDateTime.parse("04.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask2 = taskManager.createNewSubtask(subtask2); // id=5
        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3, Эпик 1", TaskState.NEW, LocalDateTime.parse("05.01.2025 11:00", dateTimeFormatter), Duration.ofMinutes(30L), addedEpic1.getId());
        addedSubtask3 = taskManager.createNewSubtask(subtask3); // id=6
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> history = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertEquals(200, response.statusCode(), "Статусы ответа не совпадает");
        assertNotNull(history, "история не возвращается");
        assertEquals(3, history.size(), "Некорректное количество задач в истории");
    }
}