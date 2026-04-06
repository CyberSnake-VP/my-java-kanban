package tasks;

import status.Status;

import java.time.Duration;
import java.time.Instant;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String name, String description, Status status, Instant startTime, Duration duration, Epic epic) {
        super(name, description, status, startTime, duration);
        this.epicId = epic.getId();
    }
    public Subtask(Integer id, String name, String description, Status status, Instant startTime, Duration duration, Epic epic) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epic.getId();
    }
    public Subtask(String name, String description, Instant startTime, Duration duration, Epic epic) {
        super(name, description, startTime, duration);
        this.epicId = epic.getId();
    }

    public Subtask(Subtask subtask) {
        super(subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());
        this.epicId = subtask.getEpicId();
    }


    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTimeToString() +
                ", duration=" + getDurationToString() +
                ", endTime=" + getEndTimeToString() +
                ", epicId=" + epicId +
                '}';
    }
}
