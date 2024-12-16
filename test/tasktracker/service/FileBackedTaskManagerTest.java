package tasktracker.service;

import org.junit.jupiter.api.Test;
import tasktracker.exceptions.ManagerSaveException;
import tasktracker.model.Task;
import tasktracker.model.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    FileBackedTaskManager fileBackedTaskManager;

    @Test
    void shouldLoadFile() {
        try {
            File tempFile = File.createTempFile("testFile1", ".csv");
            Writer fileWriter = getFileWriter(tempFile);
            fileWriter.close();

            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Во время чтения файла произошла ошибка");
        }
        assertEquals(TaskType.TASK, fileBackedTaskManager.getAllTasks().getFirst().getTaskType());
        assertEquals("Epic2", fileBackedTaskManager.getAllEpics().getFirst().getName());
        assertEquals(3, fileBackedTaskManager.getAllSubtasks().getFirst().getId());
    }

    private static Writer getFileWriter(File tempFile) throws IOException {
        Writer fileWriter = new FileWriter(tempFile);
        fileWriter.write("id" + "," + "type" + "," + "name" + "," + "status" + "," + "description" + "," + "epic" + '\n');
        fileWriter.write("1" + "," + "TASK" + "," + "Task1" + "," + "NEW" + "," + "Description task1" + "," + "\n");
        fileWriter.write("2" + "," + "EPIC" + "," + "Epic2" + "," + "DONE" + "," + "Description epic2" + "," + "\n");
        fileWriter.write("3" + "," + "SUBTASK" + "," + "Sub Task2" + "," + "DONE" + "," + "Description sub task3" + "," + "2" + "," + "\n");
        return fileWriter;
    }

    @Test
    void shouldLoadEmptyFile() {
        try {
            File tempFile = File.createTempFile("emptyTest", ".csv");
            Writer fileWriter = new FileWriter(tempFile);
            fileWriter.close();

            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Во время чтения файла произошла ошибка");
        }

        assertTrue(fileBackedTaskManager.getAllTasks().isEmpty());
        assertTrue(fileBackedTaskManager.getAllEpics().isEmpty());
        assertTrue(fileBackedTaskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSave1TaskToFile() {
        try {
            File tempFile = File.createTempFile("testFile2", ".csv");
            fileBackedTaskManager = new FileBackedTaskManager(tempFile);
            Task task = new Task(1);
            task.setName("task");
            task.setDescription("task description");

            Task savedTask = fileBackedTaskManager.createNewTask(task);
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        } catch (IOException e) {
            throw new ManagerSaveException("Во время чтения файла произошла ошибка");
        }
        assertEquals(1, fileBackedTaskManager.getAllTasks().getFirst().getId());
        assertEquals("task", fileBackedTaskManager.getAllTasks().getFirst().getName());
    }

}