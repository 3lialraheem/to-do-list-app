package app;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private static final Path FILE_PATH = Paths.get("tasks.json");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final List<Task> tasks;

    public TaskManager() {
        this.tasks = loadTasks();
    }

    public void addTask(String description) {
        int nextId = tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
        String now = LocalDateTime.now().format(FORMATTER);
        Task newTask = new Task(nextId, description, "todo", now, now);
        tasks.add(newTask);
        saveTasks();
        System.out.printf("Task added successfully (ID: %d)%n", nextId);
    }

    public void updateTaskDescription(int id, String newDescription) {
        Task task = findTaskById(id);
        if (task != null) {
            task.update(newDescription, null);
            saveTasks();
            System.out.printf("Task %d updated successfully.%n", id);
        }
    }

    public void updateTaskStatus(int id, String newStatus) {
        Task task = findTaskById(id);
        if (task != null) {
            task.update(null, newStatus);
            saveTasks();
            System.out.printf("Task %d marked as %s.%n", id, newStatus);
        }
    }

    public void deleteTask(int id) {
        Task task = findTaskById(id);
        if (task != null) {
            tasks.remove(task);
            saveTasks();
            System.out.printf("Task %d deleted successfully.%n", id);
        }
    }

    public void listTasks(String filter) {
        List<Task> filtered = tasks.stream()
                .filter(t -> filter.equalsIgnoreCase("all") || t.getStatus().equalsIgnoreCase(filter))
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No matching tasks found.");
            return;
        }

        System.out.printf("%-5s | %-12s | %-30s | %-20s%n", "ID", "Status", "Description", "Updated At");
        System.out.println("-".repeat(75));
        for (Task t : filtered) {
            String desc = t.getDescription().length() <= 30 ? t.getDescription() : t.getDescription().substring(0, 27) + "...";
            System.out.printf("%-5d | %-12s | %-30s | %-20s%n", t.getId(), t.getStatus(), desc, t.getUpdatedAt());
        }
    }

    private Task findTaskById(int id) {
        Task task = tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (task == null) {
            System.out.printf("Error: Task with ID %d not found.%n", id);
        }
        return task;
    }

    // --- Fixed Persistence Methods ---

    private void saveTasks() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("[");
            for (int i = 0; i < tasks.size(); i++) {
                lines.add("  " + tasks.get(i).toJson() + (i < tasks.size() - 1 ? "," : ""));
            }
            lines.add("]");
            Files.write(FILE_PATH, lines);
        } catch (IOException e) {
            System.out.println("Error saving tasks to file: " + e.getMessage());
        }
    }

    private List<Task> loadTasks() {
        List<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) return loadedTasks;

        try {
            List<String> lines = Files.readAllLines(FILE_PATH);

            for (String line : lines) {
                line = line.trim();
                // Skip outer array brackets
                if (line.equals("[") || line.equals("]") || line.isEmpty()) continue;

                // Remove surrounding braces and trailing comma
                if (line.endsWith(",")) line = line.substring(0, line.length() - 1);
                line = line.replaceAll("^\\{|\\}$", "").trim();

                int id = 0;
                String description = "", status = "todo", createdAt = "", updatedAt = "";

                // Parse key-value pairs
                String[] pairs = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String pair : pairs) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length < 2) continue;

                    String key = kv[0].replaceAll("[\"\\s]", "");
                    String value = kv[1].trim().replaceAll("^\"|\"$", "");

                    switch (key) {
                        case "id" -> id = Integer.parseInt(value);
                        case "description" -> description = value.replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t");
                        case "status" -> status = value;
                        case "createdAt" -> createdAt = value;
                        case "updatedAt" -> updatedAt = value;
                    }
                }

                if (id > 0) {
                    loadedTasks.add(new Task(id, description, status, createdAt, updatedAt));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks from file: " + e.getMessage());
        }
        return loadedTasks;
    }
}