package Manager.Contact;

import Manager.Manager;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
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

    public void removeContact(int id) {
        namesToIds.remove(contacts.get(id).getName());
        contacts.remove(id);
        if (numPages == 1) {
            --pageSize;
        }
    }

    public void removeContactByName(String name) {
        contacts.remove(namesToIds.get(name));
        namesToIds.remove(name);
        if (numPages == 1) {
            --pageSize;
        }
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

}
