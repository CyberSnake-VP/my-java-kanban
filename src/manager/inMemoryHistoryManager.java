package manager;

import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class inMemoryHistoryManager implements HistoryManager{
    private final Map<Integer, Node> history = new HashMap<>();

    // превращаем inMemoryHistoryManager в функционал связного списка
    // просто добавляем ссылки на голову и хвост.
    private Node head;
    private Node tail;

    // Класс Node для хранения данных(просмотренных задач) двусвязный список
    private static class Node {
        private Node prev;
        private Task task;
        private Node next;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private Node addLast(Task task) {
        Node oldTail = tail;
        Node newTail = new Node(oldTail, task, null);
        tail = newTail;
        if(oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        return newTail;
    }

    @Override
    public void add(Task task) {

    }

    @Override
    public List<Task> getHistory() {
        return List.of();
    }

    @Override
    public void remove(int id) {

    }
}
