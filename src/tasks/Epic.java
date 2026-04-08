package tasks;

import status.Status;

import java.time.Instant;
import java.util.ArrayList;

public class Epic extends Task {
   Instant endTime;

   // поле id подзадач эпика
   private ArrayList<Integer> subtaskIdList = new ArrayList<>();

   public Epic(String name, String description) {
      super(name, description, Status.NEW, null, null);
      this.endTime = null;
   }

   public Epic(Epic epic) {
      super(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(), epic.getStartTime(), epic.getDuration());
      this.setSubtaskIdList(epic.getSubtaskIdList());
      this.endTime = epic.getEndTime();

   }

   public void setSubtaskIdList(ArrayList<Integer> subtaskIdList) {
      this.subtaskIdList = subtaskIdList;
   }

   public void addSubtaskIdList(Integer subtaskId) {
      this.subtaskIdList.add(subtaskId);
   }

   public ArrayList<Integer> getSubtaskIdList() {
      return subtaskIdList;
   }


   // нужно устанавливать время окончания для эпика, как самое позднее окончание его подзадачи
   public void setEndTime(Instant endTime) {
      this.endTime = endTime;
   }

   @Override
   public Instant getEndTime() {
      return endTime;
   }

   @Override
   public String toString() {
      return "Epic{" +
              "id=" + getId() +
              ", name='" + getName() + '\'' +
              ", description='" + getDescription() + '\'' +
              ", status=" + getStatus() +
              ", subtaskIdList=" + subtaskIdList +
              ", startTime=" + getStartTimeToString() +
              ", duration=" + getDurationToString() +
              ", endTime=" + getEndTimeToString() +
              '}';
   }
}
