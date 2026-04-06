package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;

import java.util.ArrayList;

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
}