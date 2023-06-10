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

    private String dbName;
    public TaskManager() {
        this.dbName = "jdbc:sqlite:conOrg.db";
        pageSize = getSize();
    }
    public TaskManager(String dbName) {
        this.dbName = dbName;
        pageSize = getSize();
    }
    private Boolean showComplete = Boolean.TRUE;

    private int numComplete = 0;

    @Override
    public void setPageSize(int size) {
        pageSize = size;
        int numTasks = getSize();
        numPages = numTasks / size + (numTasks % size == 0 ? 0 : 1);
    }

    public void addTask(String description, Date deadline, int priority) throws ArithmeticException {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.addTask(new Task(description, deadline, priority));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int numTasks = getSize();
        if (numPages == 1) {
            ++pageSize;
        } else if ((numTasks % pageSize == 1) || (pageSize == 1)) {
            ++numPages;
        }
    }

    public void addTask(String description, Date deadline, int priority, String category) throws ArithmeticException {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.addTask(new Task(description, deadline, priority, category));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int numTasks = getSize();
        if (numPages == 1) {
            ++pageSize;
        } else if ((numTasks % pageSize == 1) || (pageSize == 1)) {
            ++numPages;
        }
    }

    public int getSize() {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            return dbHandler.getSize();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Task getTask(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            return dbHandler.getTask(id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void removeTask(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.deleteTask(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int numTasks = getSize();
        if (numPages == 1) {
            --pageSize;
        } else if (numTasks % pageSize == 0) {
            --numPages;
            if (currPage == numPages) {
                prevPage();
            }
        }
    }
    public Task selectTask(int id) {
        return getTask(id);
    }

    public void markComplete(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.setComplete(id, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ++numComplete;
    }

    public void markIncomplete(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.setComplete(id, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        --numComplete;
    }

    public void toggleComplete() {
        showComplete = !showComplete;
    }

    public Boolean getCompleteShown() {
        return showComplete;
    }

    public void showTasks() {
        Map<Integer, Task> tasks;
        try {
            tasks = DbHandler.getInstanceWithName(this.dbName).getAllTasks();
        } catch (SQLException e) {
            System.out.println("Something went wrong");
            return;
        }
        if (tasks.size() - (showComplete ? 0 : 1) * numComplete == 0) {
            System.out.println("No tasks scheduled yet (or probably complete tasks display is toggled OFF)");
            return;
        }
        showTasksFromMap(tasks);
    }

    public void showByDate(Date date) {
        Map<Integer, Task> tasks;
        try {
            tasks = DbHandler.getInstanceWithName(this.dbName).getAllTasksByDate(date);
        } catch (SQLException e) {
            System.out.println("Something went wrong");
            return;
        }
        int end = tasks.size();
        if (end - (showComplete ? 0 : 1) * numComplete == 0) {
            System.out.println("No tasks scheduled yet (or probably complete tasks display is toggled OFF)");
            return;
        }
        Integer[] ids = new Integer[end];
        tasks.keySet().toArray(ids);
        Arrays.sort(ids, Comparator.comparingInt((Integer fst) -> tasks.get(fst).getPriority())
                .thenComparing(fst -> tasks.get(fst).getDescription()));
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

    public void showByCategory(String category) {
        Map<Integer, Task> tasks;
        try {
            tasks = DbHandler.getInstanceWithName(this.dbName).getAllTasksByCategory(category);
        } catch (SQLException e) {
            System.out.println("Something went wrong");
            return;
        }
        int end = tasks.size();
        if (end - (showComplete ? 0 : 1) * numComplete == 0) {
            System.out.println("No tasks scheduled yet (or probably complete tasks display is toggled OFF)");
            return;
        }
        showTasksFromMap(tasks);
    }

    private void showTasksFromMap(Map<Integer, Task> tasks) {
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

        public static synchronized DbHandler getInstanceWithName(String dbName) throws SQLException {
            if (instance == null)
                instance = new DbHandler(dbName);
            return instance;
        }

        private final Connection connection;

        private DbHandler() throws SQLException {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
            CreateDB();
        }

        private DbHandler(String dbName) throws SQLException {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(dbName);
            CreateDB();
        }
        public Task getTask(int id) throws SQLException {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT id, description, deadline, complete, priority, category FROM Tasks WHERE id = ?")) {
                statement.setObject(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    return null;
                }
                return new Task(
                        resultSet.getInt("id"),
                        resultSet.getString("description"),
                        resultSet.getDate("deadline"),
                        resultSet.getBoolean("complete"),
                        resultSet.getInt("priority"),
                        resultSet.getString("category")
                );
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Id out of bounds");
            }
        }

        public void setComplete(int id, boolean isComplete) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE Tasks SET complete=? WHERE id = ?")) {
                statement.setBoolean(1, isComplete);
                statement.setInt(2, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public Map<Integer, Task> getAllTasks() {
            try (Statement statement = this.connection.createStatement()) {
                Map<Integer, Task> tasks = new HashMap<>();
                ResultSet resultSet = statement.executeQuery("SELECT id, description, deadline, complete, priority FROM Tasks");
                while (resultSet.next()) {
                    tasks.put(resultSet.getInt("id"),
                            new Task(
                                    resultSet.getInt("id"),
                                    resultSet.getString("description"),
                                    resultSet.getTimestamp("deadline"),
                                    resultSet.getBoolean("complete"),
                                    resultSet.getInt("priority")
                    ));
                }
                return tasks;

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyMap();
            }
        }

        public Map<Integer, Task> getAllTasksByDate(Date date) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT id, description, deadline, complete, priority, category FROM Tasks " +
                            "WHERE deadline>= ? AND deadline < ?")) {
                statement.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
                statement.setTimestamp(2, new java.sql.Timestamp(date.getTime() + 24L*60*60*60*1000));
                return getIntegerTaskMap(statement);

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyMap();
            }
        }

        public Map<Integer, Task> getAllTasksByCategory(String category) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT id, description, deadline, complete, priority, category FROM Tasks " +
                            "WHERE category=?")) {
                statement.setString(1, category);
                return getIntegerTaskMap(statement);

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyMap();
            }
        }

        private Map<Integer, Task> getIntegerTaskMap(PreparedStatement statement) throws SQLException {
            Map<Integer, Task> tasks = new HashMap<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                tasks.put(resultSet.getInt("id"),
                        new Task(
                                resultSet.getInt("id"),
                                resultSet.getString("description"),
                                resultSet.getTimestamp("deadline"),
                                resultSet.getBoolean("complete"),
                                resultSet.getInt("priority"),
                                resultSet.getString("category")
                        ));
            }
            return tasks;
        }

        public void CreateDB() {
            try (Statement statement = this.connection.createStatement()) {
                statement.execute("CREATE TABLE if not exists 'Tasks' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'description' text, 'deadline' Timestamp, 'complete' BOOLEAN, 'priority' INTEGER, 'category' text);");
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
                    "INSERT INTO Tasks('description', 'deadline', 'complete', 'priority', 'category') " +
                            "VALUES(?, ?, ?, ?, ?)")) {
                statement.setObject(1, task.getDescription());
                statement.setObject(2, task.getDeadline());
                statement.setObject(3, task.isComplete());
                statement.setObject(4, task.getPriority());
                statement.setObject(5, task.getCategory());
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

        public int getSize() {
            try (Statement statement = this.connection.createStatement()) {
                ResultSet result = statement.executeQuery("SELECT count(*) FROM Tasks");
                return result.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
