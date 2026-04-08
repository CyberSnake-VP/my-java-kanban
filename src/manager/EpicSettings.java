package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class EpicSettings {
    /**
     * Утилитарный класс
     * Определяет статус эпика с учетом его подзадач.
     * Определяет время начала, продолжительность, окончание выполнения
     */


    public static void setStatus(Epic epic, ArrayList<Subtask> subtasks) {
        /** если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
         если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
         во всех остальных случаях статус должен быть IN_PROGRESS. */

        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        // флаги для анализа состояния подзадач у эпика
        boolean isDone = false;
        boolean isNew = false;
        boolean isProgress = false;

        for (Subtask subtask : subtasks) {
            switch (subtask.getStatus()) {
                case NEW:
                    isNew = true;
                    break;
                case DONE:
                    isDone = true;
                    break;
                case IN_PROGRESS:
                    isProgress = true;
                    break;
            }
        }

        if(isProgress || (isNew && isDone)) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }

        if(isNew) {
            epic.setStatus(Status.NEW);
            return;
        }
        epic.setStatus(Status.DONE);
    }


    public static void setEpicTime(Epic epic, ArrayList<Subtask> subtasks) {

        if(subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }

        // устанавливаем значение время начала для эпика, если его нет то null
        epic.setStartTime(
                subtasks.stream()
                        .map(Subtask::getStartTime)
                        .filter(Objects::nonNull)
                        .min(Instant::compareTo)
                        .orElse(null)
        );
        // так же с продолжительностью
        epic.setDuration(
                subtasks.stream()
                        .map(Subtask::getDuration)
                        .filter(Objects::nonNull)
                        .reduce(Duration.ZERO, Duration::plus)
        );
        // с временем окончания
        epic.setEndTime(
                subtasks.stream()
                        .map(Subtask::getEndTime)
                        .filter(Objects::nonNull)
                        .max(Instant::compareTo)
                        .orElse(null)
        );
    }
}