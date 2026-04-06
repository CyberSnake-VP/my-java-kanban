import manager.InMemoryTaskManager;
import manager.TaskManager;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
       ArrayList<Integer> numbers = new ArrayList<>();
       numbers.add(1);
       numbers.add(2);
       System.out.println(numbers);
       numbers.set(1, 3);
       System.out.println(numbers);

       for(Integer number : numbers) {
           if(number == 1) {
               numbers.set(1, 23);
           }
           System.out.println(number);
       }
    }
}
