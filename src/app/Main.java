package app;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager manager = new TaskManager();

        System.out.println("--- Task Tracker Started ---");

        while (true) {
            printMenu();
            System.out.print("> Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine().trim();
                    if (description.isEmpty()) {
                        System.out.println("Error: Description cannot be empty.");
                    } else {
                        manager.addTask(description);
                    }
                }
                case "2" -> {
                    int id = promptForId(scanner);
                    if (id != -1) {
                        System.out.print("Enter new description: ");
                        String newDesc = scanner.nextLine().trim();
                        manager.updateTaskDescription(id, newDesc);
                    }
                }
                case "3" -> {
                    int id = promptForId(scanner);
                    if (id != -1) manager.deleteTask(id);
                }
                case "4" -> {
                    int id = promptForId(scanner);
                    if (id != -1) manager.updateTaskStatus(id, "in-progress");
                }
                case "5" -> {
                    int id = promptForId(scanner);
                    if (id != -1) manager.updateTaskStatus(id, "done");
                }
                case "6" -> {
                    System.out.println("List options: [all, todo, in-progress, done]");
                    System.out.print("Filter by status (default 'all'): ");
                    String filter = scanner.nextLine().trim();
                    manager.listTasks(filter.isEmpty() ? "all" : filter);
                }
                case "7" -> {
                    System.out.println("Exiting Task Tracker. Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option. Please enter a number between 1 and 7.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("==========================================");
        System.out.println("1. Add Task");
        System.out.println("2. Update Task");
        System.out.println("3. Delete Task");
        System.out.println("4. Mark Task as In Progress");
        System.out.println("5. Mark Task as Done");
        System.out.println("6. List Tasks");
        System.out.println("7. Exit");
        System.out.println("==========================================");
    }

    private static int promptForId(Scanner scanner) {
        System.out.print("Enter task ID: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Task ID must be a valid number.");
            return -1;
        }
    }
}