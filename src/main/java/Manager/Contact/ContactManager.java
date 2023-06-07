package Manager.Contact;

import Manager.Manager;
import Manager.Task.Task;
import Manager.Task.TaskManager;
import org.apache.commons.lang3.StringUtils;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class ContactManager extends Manager {

    private String dbName;

    public ContactManager() {
        this.dbName = "jdbc:sqlite:conOrg.db";
        pageSize = getSize();
    }
    public ContactManager(String dbName) {
        this.dbName = dbName;
        pageSize = getSize();
    }

    @Override
    public void setPageSize(int size) {
        pageSize = size;
        int numTasks = getSize();
        numPages = numTasks / size + (numTasks % size == 0 ? 0 : 1);
    }

    public void addContact(String name, String phoneNumber, String email, String address) throws ArithmeticException {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.addContact(new Contact(name, phoneNumber, email, address));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int numContacts = getSize();
        if (numPages == 1) {
            ++pageSize;
        } else if (numContacts % pageSize == 1) {
            ++numPages;
        }
    }

    public Contact getContact(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            return dbHandler.getContact(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Contact getContactByName(String name) {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            return dbHandler.getContactByName(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeContact(int id) {
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.deleteContact(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int numContacts = getSize();
        if (numPages == 1) {
            --pageSize;
        } else if (numContacts % pageSize == 0) {
            --numPages;
            if (currPage == numPages) {
                prevPage();
            }
        }
    }

    public void removeContactByName(String name) {
        if (numPages == 1) {
            setPageSize(getPageSize() - 1);
        } else if (getSize() % numPages == 1) {
            numPages -= 1;
            prevPage();
        }
        try {
            DbHandler dbHandler = DbHandler.getInstanceWithName(this.dbName);
            dbHandler.deleteContactByName(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSize() {
        Map<Integer, Contact> contacts;
        try {
            return DbHandler.getInstanceWithName(this.dbName).getSize();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void showContacts() {
        Map<Integer, Contact> contacts;
        try {
            contacts = DbHandler.getInstanceWithName(this.dbName).getAllContacts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Integer[] keys = new Integer[contacts.size()];
        contacts.keySet().toArray(keys);
        int start = currPage * pageSize;
        int end = Math.min((currPage + 1) * pageSize, contacts.size());
        for (int i = start; i < end; ++i) {
            contacts.get(keys[i]).display();
            if (i < end - 1) {
                System.out.println(StringUtils.repeat('=', 70));
            }
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

        public Contact getContact(int id) throws SQLException {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT id, name, phoneNumber, email, address FROM Contacts WHERE id = ?")) {
                statement.setObject(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    return null;
                }
                return new Contact(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email"),
                        resultSet.getString("address"));
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Id out of bounds");
            }
        }

        public Contact getContactByName(String name) throws SQLException {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT id, name, phoneNumber, email, address FROM Contacts WHERE name = ?")) {
                statement.setObject(1, name);
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    return null;
                }
                return new Contact(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email"),
                        resultSet.getString("address"));
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Id out of bounds");
            }
        }

        public Map<Integer, Contact> getAllContacts() {
            try (Statement statement = this.connection.createStatement()) {
                Map<Integer, Contact> contacts = new HashMap<>();
                ResultSet resultSet = statement.executeQuery("SELECT id, name, phoneNumber, email, address FROM Contacts");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    contacts.put(id,
                            new Contact(id,
                            resultSet.getString("name"),
                            resultSet.getString("phoneNumber"),
                            resultSet.getString("email"),
                            resultSet.getString("address")
                    ));
                }
                // Возвращаем наш список
                return contacts;

            } catch (SQLException e) {
                e.printStackTrace();
                // Если произошла ошибка - возвращаем пустую коллекцию
                return Collections.emptyMap();
            }
        }

        public void CreateDB() {
            try (Statement statement = this.connection.createStatement()) {
                statement.execute("CREATE TABLE if not exists 'Contacts' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'phoneNumber' text, 'email' text, 'address' text);");
            }
            catch (SQLException e) {
                e.printStackTrace();
                // Если произошла ошибка - возвращаем пустую коллекцию
            }
        }

        // Добавление продукта в БД
        public void addContact(Contact contact) {
            // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO Contacts('name', 'phoneNumber', 'email', 'address') " +
                            "VALUES(?, ?, ?, ?)")) {
                if (!isNull(getContactByName(contact.getName()))) {
                    throw new RuntimeException("Contact is already exists");
                }

                statement.setObject(1, contact.getName());
                statement.setObject(2, contact.getPhoneNumber());
                statement.setObject(3, contact.getEmail());
                statement.setObject(4, contact.getAddess());
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void deleteContact(int id) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "DELETE FROM Contacts WHERE id = ?")) {
                statement.setObject(1, id);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void deleteContactByName(String name) {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "DELETE FROM Contacts WHERE name = ?")) {
                statement.setObject(1, name);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public int getSize() {
            try (Statement statement = this.connection.createStatement()) {
                ResultSet result = statement.executeQuery("SELECT count(*) FROM Contacts");
                return result.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
