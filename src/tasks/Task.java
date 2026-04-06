package tasks;

import status.Status;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private Instant startTime;


    //  конструктор один
    public Task(String name, String description, Status status, Duration duration, Instant startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // конструктор копирования другой задачи
    public Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    // конструктор с передачей id
    public Task(Integer id, String name, String description, Status status, Duration duration, Instant startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // конструктор без передачи статуса задачи и id
    public Task(String name, String description, Duration duration, Instant startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }


    // вывод duration в минутах в строчном представлении
    public String getDurationToString() {
        if (duration != null) {
            return duration.toMinutes() + "";
        }
        return null;

    }

    // получение строчного представления начала задачи
    public String getStartTimeToString() {
        if (startTime != null) {
            return startTime.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm"));
        }
        return null;
    }

    public Instant getEndTime() {
        if(startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public String getEndTimeToString() {
        if(getEndTime() != null) {
            return getEndTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm"));
        }
        return null;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + getDurationToString() +
                ", startTime=" + getStartTimeToString() +
                ", endTime=" + getEndTimeToString() +
                '}';
    }
}
