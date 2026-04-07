package manager;

import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager>{

    @BeforeEach
    @Override
    void setUp() {
       manager = Managers.getDefault();
       task = new Task("name", "description", Instant.now(), Duration.ofMinutes(10));
       epic = new Epic("name", "description");
    }
}
