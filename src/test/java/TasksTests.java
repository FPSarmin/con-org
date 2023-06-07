import Manager.Task.Task;
import Manager.Task.TaskManager;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("All tests")
public class TasksTests {

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    @Test
    @DisplayName("Test task class methods")
    public void TaskMethodsTest() {


        int id = 0;
        boolean exception = false;
        String description = "Write tests for java project";
        Date date;
        try {
            date = formatter.parse("06-06-2023 18:10");
        } catch (ParseException e) {
            date = new Date();
            exception = true;
        }
        int priority = 1;
        assertFalse(exception);

        Task task = new Task(id, description, date, priority);
        // Check getters
        assertEquals(task.getId(), 0);
        assertEquals(task.getDescription(), "Write tests for java project");
        assertEquals(task.getDeadline(), new Date(1686064200000L));
        assertFalse(task.isComplete());
        assertEquals(task.getPriority(), 1);
        // Check setters
        task.setDescription("NIS");
        assertEquals(task.getDescription(), "NIS");
        try {
            date = formatter.parse("07-06-2023 18:10");
        } catch (ParseException e) {
            exception = true;
        }
        assertFalse(exception);
        task.setDeadline(date);
        assertEquals(task.getDeadline(), new Date(1686064200000L + 24L*60L*60L*1000L));
        task.setPriority(2);
        assertEquals(task.getPriority(), 2);
        task.setComplete();
        assertTrue(task.isComplete());
        task.setIncomplete();
        assertFalse(task.isComplete());
    }

    TaskManager tm = new TaskManager("jdbc:sqlite:TestConOrg.db");

    @Test
    @DisplayName("Test AddTask and GetTask methods")
    public void AddTasks() {
        Task task = tm.getTask(1);
        assertNull(task);
        tm.addTask("Write tests for java project", new Date(1686064200000L), 1);
        tm.addTask("NIS", new Date(1686064200000L + 24L*60L*60L*1000L), 2);
        task = tm.getTask(1);
        Task finalTask = task;
        assertAll(
            () -> assertNotNull(finalTask),
            () -> assertEquals("Write tests for java project", finalTask.getDescription()),
            () -> assertEquals(finalTask.getDeadline(), new Date(1686064200000L)),
            () -> assertEquals(finalTask.getPriority(), 1),
            () -> assertFalse(finalTask.isComplete())
        );
        task = tm.getTask(2);
        Task finalTask1 = task;
        assertAll(
                () -> assertNotNull(finalTask1),
                () -> assertEquals("NIS", finalTask1.getDescription()),
                () -> assertEquals(new Date(1686064200000L + 24L*60L*60L*1000L), finalTask1.getDeadline()),
                () -> assertEquals(2, finalTask1.getPriority()),
                () -> assertFalse(finalTask.isComplete())
        );
    }

    @Test
    @DisplayName("Test MarkComplete/Incomplete")
    public void Mark() {
        // Check if completion completes correct task
        tm.markComplete(1);
        Task task = tm.getTask(1);
        assertTrue(task.isComplete());
        task = tm.getTask(2);
        assertFalse(task.isComplete());
        tm.markComplete(2);
        task = tm.getTask(2);
        assertTrue(task.isComplete());
        tm.markIncomplete(2);
        task = tm.getTask(2);
        assertFalse(task.isComplete());
        task = tm.getTask(1);
        assertTrue(task.isComplete());
        tm.markIncomplete(1);
        task = tm.getTask(1);
        assertFalse(task.isComplete());

        // Check toggle complete
        boolean shown = tm.getCompleteShown();
        tm.toggleComplete();
        assertEquals(tm.getCompleteShown(), !shown);
        tm.toggleComplete();
        assertEquals(tm.getCompleteShown(), shown);
    }

    @Test
    @DisplayName("Test RemoveTask and GetTask methods")
    public void RemoveTasks() {
        tm.removeTask(2);
        Task task = tm.getTask(1);
        assertNotNull(task);
        task = tm.getTask(2);
        assertNull(task);
        tm.removeTask(1);
        task = tm.getTask(1);
        assertNull(task);
    }

}
