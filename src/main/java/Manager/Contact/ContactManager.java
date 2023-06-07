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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactManager extends Manager {
    private Map<Integer, Contact> contacts = new HashMap<>();
    private Map<String, Integer> namesToIds = new HashMap<>();

    @Override
    public void setPageSize(int size) {
        pageSize = size;
        numPages = contacts.size() / size + (contacts.size() % size == 0 ? 0 : 1);
    }
    public void addContact(String name, String phoneNumber, String email, String address) throws ArithmeticException {
        int id = contacts.size();
        if (id == Integer.MAX_VALUE) {
            throw new ArithmeticException("Max contacts reached");
        }
        Contact contact = new Contact(id, name, phoneNumber, email, address);
        contacts.put(id, contact);
        namesToIds.put(name, id);
        if (numPages == 1) {
            ++pageSize;
        }
    }

    public Contact getContact(int id) {
        return contacts.get(id);
    }

    public Contact getContactByName(String name) {
        return contacts.get(namesToIds.get(name));
    }

    public void removeContact(int id) {
        if (!contacts.containsKey(id)) {
            return;
        }
        namesToIds.remove(contacts.get(id).getName());
        contacts.remove(id);
        if (numPages == 1) {
            --pageSize;
        }
    }

    public void removeContactByName(String name) {
        if (!namesToIds.containsKey(name)) {
            return;
        }
        if (numPages == 1) {
            setPageSize(getPageSize() - 1);
        } else if (getSize() % numPages == 1) {
            numPages -= 1;
            prevPage();
        }
        contacts.remove(namesToIds.get(name));
        namesToIds.remove(name);
    }

    public int getSize() {
        return contacts.size();
    }

    public void showContacts() {
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

        private final Connection connection;

        private DbHandler() throws SQLException {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
            CreateDB();
        }

        public List<Contact> getAllContacts() {
            try (Statement statement = this.connection.createStatement()) {
                List<Contact> contacts = new ArrayList<>();
                ResultSet resultSet = statement.executeQuery("SELECT name, phoneNumber, email, address FROM Contacts");
                while (resultSet.next()) {
                    contacts.add(new Contact(resultSet.getInt("id"),
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
                return Collections.emptyList();
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
        public void addTask(Contact contact) {
            // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO Contacts('name', 'phoneNumber', 'email', 'address') " +
                            "VALUES(?, ?, ?, ?)")) {
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
    }
}
