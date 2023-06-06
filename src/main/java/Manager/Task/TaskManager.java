package Manager.Task;

import Manager.Manager;
import org.apache.commons.lang3.StringUtils;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class TaskManager extends Manager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Date, Set<Integer>> deadlines = new HashMap<>();

    private Boolean showComplete = Boolean.TRUE;

    private int numComplete = 0;

    @Override
    public void setPageSize(int size) {
        pageSize = size;
        numPages = tasks.size() / size + (tasks.size() % size == 0 ? 0 : 1);
    }

    public void addTask(String description, Date deadline, int priority) throws ArithmeticException {
        try {
            DbHandler dbHandler = DbHandler.getInstance();
            dbHandler.addTask(new Task(description, deadline, priority));
            updateParams();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Task getTask(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstance();
            return dbHandler.getTask(id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void updateParams() {
        List<Task> tasksFromDb = new ArrayList<>();
        try {
            DbHandler dbHandler = DbHandler.getInstance();
            tasksFromDb = dbHandler.getAllTasks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Task task: tasksFromDb) {
            if (task.getId() == Integer.MAX_VALUE) {
                throw new ArithmeticException("Max contacts reached");
            }
            tasks.put(task.getId(), task);
            if (!deadlines.containsKey(task.getDeadline())) {
                deadlines.put(task.getDeadline(), new HashSet<>());
            }
            deadlines.get(task.getDeadline()).add(task.getId());
            if (numPages == 1) {
                ++pageSize;
            }
        }
    }

    public void removeTask(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstance();
            dbHandler.deleteTask(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateParams();
        //deadlines.get(tasks.get(id).getDeadline()).remove(id);
        if (tasks.get(id).isComplete()) {
            --numComplete;
        }
        tasks.remove(id);
        if (numPages == 1) {
            --pageSize;
        }
    }
    public Task selectTask(int id) {
        return tasks.get(id);
    }

    public void markComplete(int id) {
        tasks.get(id).setComplete();
        ++numComplete;
    }

    public void markIncomplete(int id) {
        tasks.get(id).setIncomplete();
        --numComplete;
    }

    public void toggleComplete() {
        showComplete = !showComplete;
    }

    public Boolean getCompleteShown() {
        return showComplete;
    }

    public void showTasks() {
        if (tasks.size() - (showComplete ? 0 : 1) * numComplete == 0) {
            System.out.println("No tasks scheduled yet (or probably complete tasks display is toggled OFF)");
            return;
        }
        Integer[] keys = new Integer[tasks.size()];
        tasks.keySet().toArray(keys);
        Arrays.sort(keys, (fst, snd) -> {
            int deadlineDiff = tasks.get(fst).getDeadline().compareTo(tasks.get(snd).getDeadline());
            if (deadlineDiff != 0) {
                return deadlineDiff;
            }
            int priorityDiff = tasks.get(fst).getPriority() - tasks.get(snd).getPriority();
            if (priorityDiff != 0) {
                return priorityDiff;
            }
            return tasks.get(fst).getDescription().compareTo(tasks.get(snd).getDescription());
        });
        int start = currPage * pageSize;
        int end = Math.min((currPage + 1) * pageSize, tasks.size());
        int lastPageEls = tasks.size() % pageSize;
        System.out.printf("Page %d of %d | %d elements\n", currPage+1, numPages, currPage == numPages-1 ? (lastPageEls == 0 ? pageSize : lastPageEls): pageSize);
        for (int i = start; i < end; ++i) {
            if (i == 0) {
                System.out.println(StringUtils.repeat("#", 70));
            }
            Task currTask = tasks.get(keys[i]);
            if (currTask.isComplete() && !showComplete) {
                continue;
            }
            currTask.display();
            System.out.println(StringUtils.repeat("#", 70));
        }
        System.out.printf("Page %d of %d | %d elements\n", currPage+1, numPages, currPage == numPages-1 ? (lastPageEls == 0 ? pageSize : lastPageEls): pageSize);
    }

    public void showByDate(Date date) {
        int end = deadlines.get(date).size();
        if (end - (showComplete ? 0 : 1) * numComplete == 0) {
            System.out.println("No tasks scheduled yet (or probably complete tasks display is toggled OFF)");
            return;
        }
        Integer[] ids = new Integer[end];
        deadlines.get(date).toArray(ids);
        Arrays.sort(ids, (fst, snd) -> {
            int priorityDiff = tasks.get(fst).getPriority() - tasks.get(snd).getPriority();
            if (priorityDiff != 0) {
                return priorityDiff;
            }
            return tasks.get(fst).getDescription().compareTo(tasks.get(snd).getDescription());
        });
        for (int i = 0; i < end; ++i) {
            if (i == 0) {
                System.out.println(StringUtils.repeat("#", 70));
            }
            Task currTask = tasks.get(ids[i]);
            if (currTask.isComplete() && !showComplete) {
                continue;
            }
            currTask.display();
            System.out.println(StringUtils.repeat("#", 70));
        }
    }
    public static class DbHandler {

        // Константа, в которой хранится адрес подключения
        private static final String CON_STR = "jdbc:sqlite:conOrg.db";

        // Используем шаблон одиночка, чтобы не плодить множество
        // экземпляров класса DbHandler
        private static DbHandler instance = null;

        public static synchronized DbHandler getInstance() throws SQLException {
            if (instance == null)
                instance = new DbHandler();
            return instance;
        }

        private final Connection connection;

        private DbHandler() throws SQLException {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
            CreateDB();
        }

        public Task getTask(int id) throws SQLException {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT description, deadline, complete, priority FROM Tasks WHERE id = ?")) {
                statement.setObject(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    return null;
                }
                String description = resultSet.getString("description");
                Date date = resultSet.getDate("deadline");
                boolean complete = resultSet.getBoolean("complete");
                int priority = resultSet.getInt("priority");
                Task task = new Task(id, description, date, priority);
                if (complete) {
                    task.setComplete();
                }
                return task;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Id out of bounds");
            }
        }

        public List<Task> getAllTasks() {
            try (Statement statement = this.connection.createStatement()) {
                List<Task> tasks = new ArrayList<>();
                ResultSet resultSet = statement.executeQuery("SELECT id, description, deadline, complete, priority FROM Tasks");
                while (resultSet.next()) {
                    tasks.add(new Task(resultSet.getInt("id"),
                            resultSet.getString("description"),
                            resultSet.getDate("deadline"),
                            resultSet.getBoolean("complete"),
                            resultSet.getInt("priority")
                    ));
                }
                // Возвращаем наш список
                return tasks;

            } catch (SQLException e) {
                e.printStackTrace();
                // Если произошла ошибка - возвращаем пустую коллекцию
                return Collections.emptyList();
            }
        }

        public void CreateDB() {
            try (Statement statement = this.connection.createStatement()) {
                statement.execute("CREATE TABLE if not exists 'Tasks' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'description' text, 'deadline' DATE, 'complete' BOOLEAN, 'priority' INTEGER);");
            }
            catch (SQLException e) {
                e.printStackTrace();
                // Если произошла ошибка - возвращаем пустую коллекцию
            }
        }

        // Добавление продукта в БД
        public void addTask(Task task) {
            // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO Tasks('description', 'deadline', 'complete', 'priority') " +
                            "VALUES(?, ?, ?, ?)")) {
                statement.setObject(1, task.getDescription());
                statement.setObject(2, task.getDeadline());
                statement.setObject(3, task.isComplete());
                statement.setObject(4, task.getPriority());
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void deleteTask(int id) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "DELETE FROM Tasks WHERE id = ?")) {
                statement.setObject(1, id);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
