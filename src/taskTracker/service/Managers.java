package taskTracker.service;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
