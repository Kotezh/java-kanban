package taskTracker.service;

import taskTracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager {
    private Map<Integer, Node<T>> viewedTasks = new HashMap<>();
    private Node<T> head;
    private Node<T> tail;

    public InMemoryHistoryManager() {
        this.viewedTasks = new HashMap<>();
    }

    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (viewedTasks.containsKey(task.getId())) {
            Node<T> node = viewedTasks.remove((task.getId()));
            this.removeNode(node);
        }
        this.linkLast((T) task);
    }

    public List<T> getHistory() {
        List<T> tasks = new ArrayList<>();
        List<Node> nodes = this.getTasks();
        for (Node node : nodes) {
            tasks.add((T) node.task);
        }

        return tasks;
    }

    @Override
    public void remove(int id) {
        if (viewedTasks.containsKey(id)) {
            Node<T> node = viewedTasks.get(id);
            this.removeNode(node);
            viewedTasks.remove(id);
        }
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
            viewedTasks.remove(node.task.getId());
            return;
        }
        if (prevNode != null && nextNode != null) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        } else if (prevNode != null && nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        } else if (prevNode == null && nextNode != null) {
            head = nextNode;
            nextNode.prev = null;
        }
        viewedTasks.remove(node.task.getId());
    }

    public void linkLast(T task) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        int taskId = task.getId();
        viewedTasks.put(taskId, newNode);
    }

    public List<Node> getTasks() {
        List<Node> nodes = new ArrayList<>();
        Node node = head;
        while (node != null) {
            nodes.add(node);
            node = node.next;
        }

        return nodes;
    }

    private static class Node<T extends Task> {
        Node<T> prev;
        Node<T> next;
        T task;

        public Node(Node<T> prev, T task, Node<T> next) {
            this.prev = prev;
            this.next = next;
            this.task = task;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(prev, node.prev) && Objects.equals(next, node.next) && Objects.equals(task, node.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prev, next, task);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "prev=" + prev +
                    ", next=" + next +
                    ", task=" + task +
                    '}';
        }
    }
}
