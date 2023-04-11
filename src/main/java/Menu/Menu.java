package Menu;

import Manager.Task.Task;
import Manager.Task.TaskManager;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Menu {
    private final TaskManager tm = new TaskManager();
    private final Scanner scanner = new Scanner(System.in);
    //private final ContactManager cm = new ContactManager();

    public void openTaskManager() throws ParseException {

        final String menuCommands = """
            ======================================================================
            == Available commands:       showAll, showByDate, addTask, editTask ==
            ==                                removeTask, toggleCompleteDisplay ==
            ==                           nextPage, editPageSize, prevPage, menu ==
            ======================================================================
            """;

        final String editCommands = """
             ======================================================================
             == Available commands:  editDescription, editDeadline, editPriority ==
             ==                                       setComplete, setIncomplete ==
             ======================================================================
             """;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        String command;
        while (true) {
            System.out.print(menuCommands);
            if ((command = scanner.nextLine()).equals("menu")) {
                break;
            }

            switch (command) {

                case "addTask" -> {

                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter date in format dd-MM-yyyy HH:mm: ");
                    String datestr = scanner.nextLine();
                    Date deadline = formatter.parse(datestr);
                    System.out.print("Enter priority: ");
                    int priority = Integer.parseInt(scanner.nextLine());
                    tm.addTask(description, deadline, priority);

                    System.out.println("Task added");

                }

                case "editTask" -> {

                    System.out.print("Enter task id: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    Task currTask = tm.selectTask(id);

                    System.out.print(editCommands);
                    String ecommand = scanner.nextLine();
                    System.out.println(ecommand);

                    switch (ecommand) {
                        case "setComplete" -> {
                            tm.markComplete(id);
                        }
                        case "setIncomplete" -> {
                            tm.markIncomplete(id);
                        }
                        case "editDescription" -> {
                            System.out.print("Enter new description: ");
                            String newDesc = scanner.nextLine();
                            currTask.setDescription(newDesc);
                        }
                        case "editDeadline" -> {
                            System.out.print("Enter date in format dd-MM-yyyy HH:mm: ");
                            String newDatestr = scanner.nextLine();
                            Date newDeadline = formatter.parse(newDatestr);
                            currTask.setDeadline(newDeadline);
                            System.out.print("Enter priority: ");
                        }
                        case "editPriority" -> {
                            int newPriority = Integer.parseInt(scanner.nextLine());
                            currTask.setPriority(newPriority);
                        }

                        default -> {
                            System.out.println("Unknown command");
                            System.out.print(editCommands);
                        }
                    }

                    System.out.println("task Edited");
                    currTask.display();

                }

                case "removeTask" -> {

                    System.out.print("Enter task id: ");
                    int rmid = Integer.parseInt(scanner.nextLine());
                    tm.removeTask(rmid);

                    System.out.println("Task removed");

                }

                case "toggleCompleteDisplay" -> {

                    tm.toggleComplete();
                    String isShown = StringUtils.repeat("ON", tm.getCompleteShown() ? 1 : 0) + StringUtils.repeat("OFF", tm.getCompleteShown() ? 0 : 1);
                    System.out.printf("Complete tasks display is %s\n", isShown);

                }

                case "nextPage" -> {

                    tm.nextPage();

                }

                case "prevPage" -> {

                    tm.prevPage();

                }

                case "editPageSize" -> {

                    System.out.print("Select new page size: ");
                    int newSize = Integer.parseInt(scanner.nextLine());
                    tm.setPageSize(newSize);

                    System.out.println("Page size edited");

                }

                case "showAll" -> {

                    tm.showTasks();

                }

                case "showByDate" -> {

                    System.out.print("Enter date in format dd-MM-yyyy HH:mm ");
                    String datestr = scanner.nextLine();
                    Date date = formatter.parse(datestr);
                    tm.showByDate(date);

                }

                default -> {

                    System.out.println("Unknown command");

                }

            }
        }
        tm.menuReturn();
    }

    public void openContactManager() {

    }

    public void showCalendar() {

    }
}
