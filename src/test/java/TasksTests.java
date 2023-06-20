import org.ConOrgApp.Manager.Task.Task;
import org.ConOrgApp.Manager.Task.TaskManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("All tests")
public class TasksTests {

    @BeforeAll
    @DisplayName("Remove test bd")
    public static void rmtestdb() {
        File testbd = new File("TestConOrg.db");
        if (testbd.delete()) {
            System.out.println("Successful delete");
        } else {
            System.out.println("Test bd not found");
        }
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    @Test
    @DisplayName("Test task class methods")
    public void TaskMethodsTests() {

        int id = 0;
        String description = "Write tests for java project";
        Date date;
        try {
            date = formatter.parse("06-06-2023 18:10");
        } catch (ParseException e) {
            date = null;
        }
        int priority = 1;
        assertNotNull(date);

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
            date = null;
        }
        assertNotNull(date);
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
        assertEquals(0, tm.getSize());
        tm.addTask("Write tests for java project", new Date(1686064200000L), 1);
        assertEquals(1, tm.getSize());
        tm.addTask("NIS", new Date(1686064200000L + 24L*60L*60L*1000L), 2);
        assertEquals(2, tm.getSize());
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

        assertEquals(2, tm.getSize());
        tm.toggleComplete();
        assertEquals(1, tm.getSize());

        task = tm.getTask(2);
        assertFalse(task.isComplete());
        tm.markComplete(2);
        task = tm.getTask(2);
        assertTrue(task.isComplete());

        assertEquals(0, tm.getSize());
        tm.toggleComplete();
        assertEquals(2, tm.getSize());

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
    @DisplayName("Test interaction with pages")
    public void TestPages() {
        // Check initials
        assertEquals(1, tm.getNumPages());
        assertEquals(0, tm.getCurrPage());
        assertEquals(2, tm.getPageSize());

        // Check size changes on add in case numPages == 1
        tm.addTask("Minor", new Date(1686064200000L + 24L*60L*60L*1000L*2L), 3);
        tm.showTasks();
        assertEquals(3, tm.getSize());

        // Check setPageSize changes pageSize correctly
        tm.setPageSize(2);
        tm.showTasks();
        assertEquals(2, tm.getNumPages());
        assertEquals(2, tm.getPageSize());
        assertEquals(0, tm.getCurrPage());

        // Check nextPage
        tm.nextPage();
        tm.showTasks();
        assertEquals(1, tm.getCurrPage());

        // Check if last page is clear it is removed
        tm.removeTask(3);
        tm.showTasks();
        assertEquals(1, tm.getNumPages());
        assertEquals(2, tm.getPageSize());

        // Check if last page is removed user currPage is not out of bounds
        assertEquals(0, tm.getCurrPage());

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

        // Just to check page values to go initial on empty tasks
        assertEquals(1, tm.getNumPages());
        assertEquals(0, tm.getCurrPage());
        assertEquals(0, tm.getPageSize());
    }

}
