package tasktracker.service;

import org.junit.jupiter.api.Test;
import tasktracker.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File tempFile;

    public FileBackedTaskManagerTest() throws IOException{
        tempFile = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldLoadFile() throws IOException {
        File tempFile = File.createTempFile("testFile1", ".csv");
        Writer fileWriter = getFileWriter(tempFile);
        fileWriter.close();

        taskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(TaskType.TASK, taskManager.getAllTasks().getFirst().getTaskType());
        assertEquals("Epic2", taskManager.getAllEpics().getFirst().getName());
        assertEquals(102, taskManager.getAllSubtasks().getFirst().getId());
    }

    private static Writer getFileWriter(File tempFile) throws IOException {
        Writer fileWriter = new FileWriter(tempFile);
        fileWriter.write("id" + "," + "type" + "," + "name" + "," + "status" + "," + "description" + "," + "start" + "," + "duration" + "," + "epic" + "\n");
        fileWriter.write("100" + "," + "TASK" + "," + "Task1" + "," + "NEW" + "," + "Description task1" + "," + "03.01.2025 11:00" + "," + "90" + "," + "\n");
        fileWriter.write("101" + "," + "EPIC" + "," + "Epic2" + "," + "DONE" + "," + "Description epic2" + "," + "04.01.2025 11:00" + "," + "100" + "," + "\n");
        fileWriter.write("102" + "," + "SUBTASK" + "," + "Sub Task2" + "," + "DONE" + "," + "Description sub task3" + "," + "05.01.2025 11:00" + "," + "20" + "," + "2" + "\n");
        return fileWriter;
    }

    @Test
    void shouldLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("emptyTest", ".csv");
        Writer fileWriter = new FileWriter(tempFile);
        fileWriter.close();

        taskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSave3TasksToFile() throws IOException {
        File tempFile = File.createTempFile("testFile2", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
        Task task = new Task("task", "task description", TaskState.NEW, "01.03.2025 11:00", 30);
        Epic epic = new Epic("epic", "epic description", TaskState.NEW);
        Subtask subtask = new Subtask("subtask", "subtask description", TaskState.NEW, "02.03.2025 11:00", 30, 2);

        Task savedTask = taskManager.createNewTask(task);
        Epic savedEpic = taskManager.createNewEpic(epic);
        Subtask savedSubtask = taskManager.createNewSubtask(subtask);

        taskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, taskManager.getAllTasks().getFirst().getId());
        assertEquals("task", taskManager.getAllTasks().getFirst().getName());
        assertEquals(2, taskManager.getAllEpics().getFirst().getId());
        assertEquals("epic", taskManager.getAllEpics().getFirst().getName());
        assertEquals(3, taskManager.getAllSubtasks().getFirst().getId());
        assertEquals("subtask", taskManager.getAllSubtasks().getFirst().getName());
    }

}