package Menu;

import Manager.Contact.ContactManager;
import Manager.Task.Task;
import Manager.Task.TaskManager;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Menu {
    private final ContactManager cm = new ContactManager();
    private final TaskManager tm = new TaskManager();
    private final Scanner scanner = new Scanner(System.in);

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private static final SimpleDateFormat FORMATTER_ONLY_DATE = new SimpleDateFormat("dd-MM-yyyy");

    public void mainMenu() throws InterruptedException {
        String command;
        String menuCommands = """
                    ======================================================================
                    == Available commands:                                   1.TaskMenu ==
                    ==                                                    2.ContactMenu ==
                    ======================================================================
                    """;
        while (true) {
            clearScreen();
            System.out.println(menuCommands);
            command = scanner.nextLine();
            switch (command) {
                case "1" -> openTaskManager();
                case "2" -> openContactManager();
                default -> System.out.println("Unknown command");
            }

        }
    }

    public void openTaskManager() throws InterruptedException {


        String command;
        while (true) {
            clearScreen();
            String menuCommands = """
                    ==============================================================================
                    == Available commands:       1.showAll, 2.showByDate, 3.addTask, 4.editTask ==
                    ==                                    5.removeTask, 6.toggleCompleteDisplay ==
                    ==                          7.nextPage, 8.editPageSize, 9.prevPage, 10.menu ==
                    ==============================================================================
                    """;
            System.out.print(menuCommands);
            if ((command = scanner.nextLine()).equals("10")) {
                return;
            }
            switch (command) {
                case "1" -> showAll("tasks");
                case "2" -> tm.showByDate(editDeadline(FORMATTER_ONLY_DATE));
                case "3" -> addTask();
                case "4" -> editTask();
                case "5" -> removeTask();
                case "6" -> toggleCompleteDisplay();
                case "7" -> tm.nextPage();
                case "8" -> editPageSize();
                case "9" -> tm.prevPage();
                default -> System.out.println("Unknown command");
            }
            tm.menuReturn();
        }
    }

    public void addTask() throws InterruptedException {
        clearScreen();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        Date deadline = editDeadline(FORMATTER);
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
                ============================================================================
                == Available commands:  1.editDescription, 2.editDeadline, 3.editPriority ==
                ==                                         4.setComplete, 5.setIncomplete ==
                ============================================================================
                """;
        System.out.print(editCommands);
        String ecommand = scanner.nextLine();
        System.out.println(ecommand);

        switch (ecommand) {
            case "1" -> {
                System.out.print("Enter new description: ");
                String newDesc = scanner.nextLine();
                currTask.setDescription(newDesc);
            }
            case "2" -> {
                currTask.setDeadline(editDeadline(FORMATTER));
                System.out.print("Enter priority: ");
            }
            case "3" -> {
                int newPriority = Integer.parseInt(scanner.nextLine());
                currTask.setPriority(newPriority);
            }
            case "4" -> tm.markComplete(id);
            case "5" -> tm.markIncomplete(id);

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

    private Date editDeadline(SimpleDateFormat format) {
        clearScreen();
        while (true) {
            try {
                System.out.print("Enter date in format " + format.toPattern() + " :");
                String newDatestr = scanner.nextLine();
                return(format.parse(newDatestr));
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
