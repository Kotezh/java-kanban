package tasktracker.service;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public InMemoryTaskManagerTest() {
        taskManager = new InMemoryTaskManager();
    }
}