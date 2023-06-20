package org.ConOrgApp.Menu;

import org.ConOrgApp.Manager.Contact.ContactManager;
import org.ConOrgApp.Manager.Task.Task;
import org.ConOrgApp.Manager.Task.TaskManager;
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
                    ==============================================================================
                    == Available commands:                                           1.TaskMenu ==
                    ==                                                            2.ContactMenu ==
                    ==                                                                   3.Exit ==
                    ==============================================================================
                    """;
        while (true) {
            clearScreen();
            System.out.print(menuCommands);
            command = scanner.nextLine();
            switch (command) {
                case "1" -> openTaskManager();
                case "2" -> openContactManager();
                case "3" -> {return;}
                default -> System.out.print("Unknown command");
            }

        }
    }

    public void openTaskManager() throws InterruptedException {


        String command;
        while (true) {
            clearScreen();
            String menuCommands = """
                    ==============================================================================
                    == Available commands:                 1.showAll, 2.showByDate, 3.showByCat ==
                    ==                                      4.addTask, 5.editTask, 6.removeTask ==
                    ==                                          7.toggleCompleteDisplay, 8.menu ==
                    ==============================================================================
                    """;
            System.out.print(menuCommands);
            command = scanner.nextLine();
            switch (command) {
                case "1" -> showAll("tasks");
                case "2" -> showAll("deadline");
                case "3" -> showAll("category");
                case "4" -> addTask();
                case "5" -> editTask();
                case "6" -> removeTask();
                case "7" -> toggleCompleteDisplay();
                case "8" -> {return;}
                default -> System.out.print("Unknown command");
            }
            tm.menuReturn();
        }
    }

    public void addTask() throws InterruptedException {
        clearScreen();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        Date deadline = editDeadline(FORMATTER);
        System.out.print("Enter priority: ");
        int priority = Integer.parseInt(scanner.nextLine());
        tm.addTask(description, deadline, priority, category);
        System.out.print("Task added");
        Thread.sleep(2 * 1000); // Wait for 2 seconds
    }

    public String getCategory() {
        System.out.print("Enter category: ");
        return scanner.nextLine();
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
                System.out.print("Unknown command\n");
                System.out.print(editCommands);
            }
        }

        System.out.print("task Edited\n");
        currTask.display();
        scanner.nextLine();
    }

    private void removeTask() throws InterruptedException {
        clearScreen();
        System.out.print("Enter task id: ");
        int rmid = Integer.parseInt(scanner.nextLine());
        tm.removeTask(rmid);

        System.out.print("Task removed\n");
        Thread.sleep(2 * 1000); // Wait for 2 seconds
    }

    private Date editDeadline(SimpleDateFormat format) {
        while (true) {
            try {
                System.out.print("Enter date in format " + format.toPattern() + " : ");
                String newDatestr = scanner.nextLine();
                return(format.parse(newDatestr));
            } catch (ParseException e) {
                System.out.print("Incorrect date type\n");
            }
        }
    }

    private void editPageSize() throws InterruptedException {
        clearScreen();
        System.out.print("Select new page size: ");
        int newSize = Integer.parseInt(scanner.nextLine());
        tm.setPageSize(newSize);

        System.out.print("Page size edited");
        Thread.sleep(2 * 1000); // Wait for 2 seconds
    }


    private void toggleCompleteDisplay() {
        clearScreen();
        tm.toggleComplete();
        String isShown = StringUtils.repeat("ON", tm.getCompleteShown() ? 1 : 0) + StringUtils.repeat("OFF", tm.getCompleteShown() ? 0 : 1);
        System.out.printf("Complete tasks display is %s\n", isShown);
        scanner.nextLine();
    }

    private void showAll(String type) throws InterruptedException{
        String command;
        while (true) {
            clearScreen();
            if (type.equals("tasks")) tm.showTasks();
            if (type.equals("deadline")) {
                Date deadline = editDeadline(FORMATTER_ONLY_DATE);
                clearScreen();
                tm.showByDate(deadline);
            }
            if (type.equals("category")) {
                String category = getCategory();
                clearScreen();
                tm.showByCategory(category);
            }
            if (type.equals("contacts")) cm.showContacts();
            System.out.print("next:\t Next page\n");
            System.out.print("size:\t Edit page size\n");
            System.out.print("prev:\t Prev page\n");
            System.out.print("back:\t Back to menu\n");
            command = scanner.nextLine();
            switch (command) {
                case "next" -> {
                    try {
                        tm.nextPage();
                    } catch (Exception ignored) {}
                }
                case "size" -> {
                    try {
                        editPageSize();
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
                default -> System.out.print("Unknown command");
            }
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void openContactManager() throws InterruptedException {
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
                default -> System.out.print("Unknown command");
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
        System.out.print("Contact added\n");
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
