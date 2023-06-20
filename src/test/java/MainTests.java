import java.io.*;
import java.io.File;
import org.ConOrgApp.Main;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTests {

    @BeforeAll
    @DisplayName("Remove test bd")
    public static void rmtestdb() {
        File testbd = new File("conOrg.db");
        if (testbd.delete()) {
            System.out.println("Successful delete");
        } else {
            System.out.println("Test bd not found");
        }
    }

    @DisplayName("Test tasks functionality")
    @Test
    public void TestAddShowEditPageSizeNextPageDeleteTask() throws IOException, InterruptedException {

        String testInput = """
            1
            4
            university
            prepare project presentation
            20-06-2023 18:10
            1
            1
            back
            4
            home
            do the cleaning
            20-06-2023 12:00
            2
            4
            university
            pick up indexing agreement
            21-06-2023 18:20
            3
            1
            size
            2
            next
            back
            2
            21-06-2023
            back
            3
            university
            back
            5
            1
            4
            q
            6
            1
            1
            back
            8
            3
            """;
        ByteArrayInputStream fileIn = new ByteArrayInputStream(testInput.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setIn(fileIn);
        System.setOut(new PrintStream(outputStream));
        String[] args = {};
        Main.main(args);
        final String ANSI_CLS_HOME = "\033[H\033[2J";
        String expected = ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                                           1.TaskMenu ==
            ==                                                            2.ContactMenu ==
            ==                                                                   3.Exit ==
            ==============================================================================
            """ + ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Enter category: " + "Enter description: " +
            "Enter date in format dd-MM-yyyy HH:mm : " + "Enter priority: " + "Task added"
            + ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Page 1 of 1 | 1 elements\n" +
            "##############################################################################\n" +
            "## Id          :                                                          1 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                               prepare project presentation ##\n" +
            "## Deadline    :                                      2023-06-20 18:10:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          1 ##\n" +
            "##############################################################################\n" +
            "Page 1 of 1 | 1 elements\n" +
            "next:	 Next page\n" +
            "size:	 Edit page size\n" +
            "prev:	 Prev page\n" +
            "back:	 Back to menu\n" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Enter category: " + "Enter description: " +
            "Enter date in format dd-MM-yyyy HH:mm : " + "Enter priority: " + "Task added" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Enter category: " + "Enter description: " +
            "Enter date in format dd-MM-yyyy HH:mm : " + "Enter priority: " + "Task added" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Page 1 of 1 | 3 elements\n" +
            "##############################################################################\n" +
            "## Id          :                                                          2 ##\n" +
            "## Category    :                                                       home ##\n" +
            "## Description :                                            do the cleaning ##\n" +
            "## Deadline    :                                      2023-06-20 12:00:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          2 ##\n" +
            "##############################################################################\n" +
            "## Id          :                                                          1 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                               prepare project presentation ##\n" +
            "## Deadline    :                                      2023-06-20 18:10:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          1 ##\n" +
            "##############################################################################\n" +
            "## Id          :                                                          3 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                                 pick up indexing agreement ##\n" +
            "## Deadline    :                                      2023-06-21 18:20:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          3 ##\n" +
            "##############################################################################\n" +
            "Page 1 of 1 | 3 elements\n" +
            "next:	 Next page\n" +
            "size:	 Edit page size\n" +
            "prev:	 Prev page\n" +
            "back:	 Back to menu\n" +
            ANSI_CLS_HOME + "Select new page size: " + "Page size edited" + ANSI_CLS_HOME + "Page 1 of 2 | 2 elements\n" +
            "##############################################################################\n" +
            "## Id          :                                                          2 ##\n" +
            "## Category    :                                                       home ##\n" +
            "## Description :                                            do the cleaning ##\n" +
            "## Deadline    :                                      2023-06-20 12:00:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          2 ##\n" +
            "##############################################################################\n" +
            "## Id          :                                                          1 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                               prepare project presentation ##\n" +
            "## Deadline    :                                      2023-06-20 18:10:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          1 ##\n" +
            "##############################################################################\n" +
            "Page 1 of 2 | 2 elements\n" +
            "next:	 Next page\n" +
            "size:	 Edit page size\n" +
            "prev:	 Prev page\n" +
            "back:	 Back to menu\n" +
            ANSI_CLS_HOME + "Page 2 of 2 | 1 elements\n" +
            "##############################################################################\n" +
            "## Id          :                                                          3 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                                 pick up indexing agreement ##\n" +
            "## Deadline    :                                      2023-06-21 18:20:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          3 ##\n" +
            "##############################################################################\n" +
            "Page 2 of 2 | 1 elements\n" +
            "next:	 Next page\n" +
            "size:	 Edit page size\n" +
            "prev:	 Prev page\n" +
            "back:	 Back to menu\n" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Enter date in format dd-MM-yyyy : "+ ANSI_CLS_HOME + "Page 1 of 1 | 1 elements\n" +
            "##############################################################################\n" +
            "## Id          :                                                          3 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                                 pick up indexing agreement ##\n" +
            "## Deadline    :                                      2023-06-21 18:20:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          3 ##\n" +
            "##############################################################################\n" +
            "Page 1 of 1 | 1 elements\n" +
            "next:	 Next page\n" +
            "size:	 Edit page size\n" +
            "prev:	 Prev page\n" +
            "back:	 Back to menu\n" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Enter category: "
            + ANSI_CLS_HOME + "Page 1 of 1 | 2 elements\n" +
            "##############################################################################\n" +
            "## Id          :                                                          1 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                               prepare project presentation ##\n" +
            "## Deadline    :                                      2023-06-20 18:10:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          1 ##\n" +
            "##############################################################################\n" +
            "## Id          :                                                          3 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                                 pick up indexing agreement ##\n" +
            "## Deadline    :                                      2023-06-21 18:20:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          3 ##\n" +
            "##############################################################################\n" +
            "Page 1 of 1 | 2 elements\n" +
            "next:	 Next page\n" +
            "size:	 Edit page size\n" +
            "prev:	 Prev page\n" +
            "back:	 Back to menu\n" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Enter task id: " + """
            ============================================================================
            == Available commands:  1.editDescription, 2.editDeadline, 3.editPriority ==
            ==                                         4.setComplete, 5.setIncomplete ==
            ============================================================================
            """ + "task Edited\n" +
            "## Id          :                                                          1 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                               prepare project presentation ##\n" +
            "## Deadline    :                                                 2023-06-20 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          1 ##\n" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME + "Enter task id: " + "Task removed\n" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ +
            ANSI_CLS_HOME + "Page 1 of 1 | 2 elements\n" +
            "##############################################################################\n" +
            "## Id          :                                                          2 ##\n" +
            "## Category    :                                                       home ##\n" +
            "## Description :                                            do the cleaning ##\n" +
            "## Deadline    :                                      2023-06-20 12:00:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          2 ##\n" +
            "##############################################################################\n" +
            "## Id          :                                                          3 ##\n" +
            "## Category    :                                                 university ##\n" +
            "## Description :                                 pick up indexing agreement ##\n" +
            "## Deadline    :                                      2023-06-21 18:20:00.0 ##\n" +
            "## Complete    :                                                         NO ##\n" +
            "## Priority    :                                                          3 ##\n" +
            "##############################################################################\n" +
            "Page 1 of 1 | 2 elements\n" +
            "next:	 Next page\n" +
            "size:	 Edit page size\n" +
            "prev:	 Prev page\n" +
            "back:	 Back to menu\n" +
            ANSI_CLS_HOME + """
            ==============================================================================
            == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
            ==                                      4.addTask, 5.editTask, 6.removeTask ==
            ==                                          7.toggleCompleteDisplay, 8.menu ==
            ==============================================================================
            """ + ANSI_CLS_HOME +"""
            ==============================================================================
            == Available commands:                                           1.TaskMenu ==
            ==                                                            2.ContactMenu ==
            ==                                                                   3.Exit ==
            ==============================================================================
            """;
        assertEquals(expected, outputStream.toString());

    }

}
