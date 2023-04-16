package Menu;

import Manager.Contact.ContactManager;
import Manager.Task.Task;
import Manager.Task.TaskManager;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Menu {
    private final ContactManager cm = new ContactManager();
    private final TaskManager tm = new TaskManager();
    private final Scanner scanner = new Scanner(System.in);

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public void mainMenu() throws InterruptedException {
        String command;
        String menuCommands = """
                    ======================================================================
                    == Available commands:                                     TaskMenu ==
                    ==                                                      ContactMenu ==
                    ======================================================================
                    """;
        while (true) {
            clearScreen();
            System.out.println(menuCommands);
            command = scanner.nextLine();
            switch (command) {
                case "TaskMenu" -> openTaskManager();
                case "ContactMenu" -> openContactManager();
                default -> System.out.println("Unknown command");
            }

        }
    }

    public void openTaskManager() throws InterruptedException {


        String command;
        while (true) {
            clearScreen();
            String menuCommands = """
                    ======================================================================
                    == Available commands:       showAll, showByDate, addTask, editTask ==
                    ==                                removeTask, toggleCompleteDisplay ==
                    ==                           nextPage, editPageSize, prevPage, menu ==
                    ======================================================================
                    """;
            System.out.print(menuCommands);
            if ((command = scanner.nextLine()).equals("menu")) {
                return;
            }
            switch (command) {
                case "addTask" -> addTask();
                case "editTask" -> editTask();
                case "removeTask" -> removeTask();
                case "toggleCompleteDisplay" -> toggleCompleteDisplay();
                case "nextPage" -> tm.nextPage();
                case "prevPage" -> tm.prevPage();
                case "editPageSize" -> editPageSize();
                case "showAll" -> showAll("tasks");
                case "showByDate" -> tm.showByDate(editDeadline());
                default -> System.out.println("Unknown command");
            }
            tm.menuReturn();
        }
    }

    public void addTask() throws InterruptedException {
        clearScreen();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        Date deadline = editDeadline();
        System.out.print("Enter priority: ");
        int priority = Integer.parseInt(scanner.nextLine());
        tm.addTask(description, deadline, priority);
        System.out.println("Task added");
        Thread.sleep(2 * 1000); // Wait for 2 seconds
    }
    public void editTask() {
        clearScreen();
        System.out.print("Enter task id: ");
        int id = Integer.parseInt(scanner.nextLine());
        Task currTask = tm.selectTask(id);

        String editCommands = """
                ======================================================================
                == Available commands:  editDescription, editDeadline, editPriority ==
                ==                                       setComplete, setIncomplete ==
                ======================================================================
                """;
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
                currTask.setDeadline(editDeadline());
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
        scanner.nextLine();
    }

    private void removeTask() {
        clearScreen();
        System.out.print("Enter task id: ");
        int rmid = Integer.parseInt(scanner.nextLine());
        tm.removeTask(rmid);

        System.out.println("Task removed");
        scanner.nextLine();
    }

    private Date editDeadline() {
        clearScreen();
        while (true) {
            try {
                System.out.print("Enter date in format dd-MM-yyyy HH:mm: ");
                String newDatestr = scanner.nextLine();
                return(FORMATTER.parse(newDatestr));
            } catch (ParseException e) {
                System.out.println("Incorrect date type");
            }
        }
    }

    private void editPageSize() {
        clearScreen();
        System.out.print("Select new page size: ");
        int newSize = Integer.parseInt(scanner.nextLine());
        tm.setPageSize(newSize);

        System.out.println("Page size edited");
        scanner.nextLine();
    }


    private void toggleCompleteDisplay() {
        clearScreen();
        tm.toggleComplete();
        String isShown = StringUtils.repeat("ON", tm.getCompleteShown() ? 1 : 0) + StringUtils.repeat("OFF", tm.getCompleteShown() ? 0 : 1);
        System.out.printf("Complete tasks display is %s\n", isShown);
        scanner.nextLine();
    }

    private void showAll(String type) {
        clearScreen();
        String command;
        while (true) {
            if (type.equals("tasks")) tm.showTasks();
            if (type.equals("contacts")) cm.showContacts();
            System.out.println("next:\t Next page");
            System.out.println("prev:\t Prev page");
            System.out.println("back:\t Back to menu");
            command = scanner.nextLine();
            switch (command) {
                case "next" -> {
                    try {
                        tm.nextPage();
                    } catch (Exception ignored) {}
                }
                case "prev" -> {
                    try {
                        tm.prevPage();
                    } catch (Exception ignored) {}
                }
                case "back" -> {
                    tm.menuReturn();
                    return;
                }
                default -> System.out.println("Unknown command");
            }
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void openContactManager() {
        String command;
        while (true) {
            String menuCommands = """
                    ======================================================================
                    == Available commands:       showAll, addContact, removeContactById ==
                    ==                                              removeContactByName ==
                    ======================================================================
                    """;
            System.out.print(menuCommands);
            if ((command = scanner.nextLine()).equals("menu")) {
                return;
            }
            switch (command) {
                case "showAll" -> showAll("contacts");
                case "addContact" -> addContact();
                case "removeContactById" ->  removeContactById();
                case "removeContactByName" -> removeContactByName();
                default -> System.out.println("Unknown command");
            }
            cm.menuReturn();
        }
    }

    private void addContact() {
        clearScreen();
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String number = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        cm.addContact(name, number, email, address);
        System.out.println("Contact added");
        scanner.nextLine();
    }

    private void removeContactById() {
        clearScreen();
        System.out.print("Enter contact id: ");
        String id = scanner.nextLine();
        cm.removeContact(Integer.parseInt(id));
        scanner.nextLine();
    }

    private void removeContactByName() {
        clearScreen();
        System.out.print("Enter contact name: ");
        String name = scanner.nextLine();
        cm.removeContactByName(name);
        scanner.nextLine();
    }
}
