package manager;

import tasks.Task;

import java.util.ArrayList;
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

    // добавление в конец
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

    // удаление ссылок на Node
    private void removeNode(Node node) {
        if(head == tail) {
            head = null;
            tail = null;
            return;
        }
        if(node == head) {
            head = head.next;
            // Проверка, что есть следующий узел(т.е. голова не единственный узел)
            if(head != null) {
                head.prev = null;
            }
            return;
        }
        if(node == tail) {
            tail = tail.prev;
            if(tail != null) {
                tail.next = null;
            }
            return;
        }

        // перезапись ссылок для других случаев
        Node prev = node.prev;
        Node next = node.next;
        prev.next = next;
        next.prev = prev;
    }

    @Override
    public void add(Task task) {
        if(task == null) {
            return;
        }
        if(history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        history.put(task.getId(), addLast(task));
    }

    @Override
    public List<Task> getHistory() {
        // пока голова не равна null добавляем задачу в список
        // Тем самым я пробегаюсь от головы к хвосту, где сохраняется порядок добавления в историю
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while(node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        // Находим ноду
        // Удаляем ссылку(связи)
        // Удаляем из таблицы истории
        /// если удалить из таблицы ноду это неправильно, потому что в самих Node'ах останутся на нее ссылки,
        ///  их нужно стереть
        if(history.containsKey(id)) {
            Node removed = history.get(id);
            removeNode(removed);
            history.remove(id);
        }
    }
}
