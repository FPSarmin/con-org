import Manager.Contact.Contact;
import Manager.Contact.ContactManager;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("All Tests")
public class ContactsTests {

    @Test
    @DisplayName("Test contact class methods")
    public void ContactMethodsTests() {
        // Check constructor
        Contact contact = new Contact(0, "Max Didur", "+7 909 908 17 11",
                                    "msdidur@edu.hse.ru", "Makovskogo 11");
        assertAll(
            () -> assertEquals(contact.getId(), 0),
            () -> assertEquals(contact.getName(), "Max Didur"),
            () -> assertEquals(contact.getPhoneNumber(), "+7 909 908 17 11"),
            () -> assertEquals(contact.getEmail(), "msdidur@edu.hse.ru"),
            () -> assertEquals(contact.getAddess(), "Makovskogo 11")
        );

        contact.setId(1);
        contact.setName("Didur Maxim Sergeevich");
        contact.setPhoneNumber("+373 779 72 303");
        contact.setEmail("d1durka@vk.com");
        contact.setAddress("Vosstanya St.");

        assertAll(
                () -> assertEquals(contact.getId(), 1),
                () -> assertEquals(contact.getName(), "Didur Maxim Sergeevich"),
                () -> assertEquals(contact.getPhoneNumber(), "+373 779 72 303"),
                () -> assertEquals(contact.getEmail(), "d1durka@vk.com"),
                () -> assertEquals(contact.getAddess(), "Vosstanya St.")
        );

    }

    ContactManager cm = new ContactManager();

    @Test
    @DisplayName("Test Contact Manager")
    public void AddContacts() {
        // Check getSize, addContact, getContact by Id/Name
        assertEquals(cm.getSize(), 0);
        cm.addContact("Max Didur", "+7 909 908 17 11",
                "msdidur@edu.hse.ru", "Makovskogo 11");
        assertEquals(cm.getSize(), 1);
        cm.addContact("Pavel Sarmin", "", "pfsarmin@edu.hse.ru", "");
        assertEquals(cm.getSize(), 2);
        cm.addContact("Fedosova Sofya", "+7 905 809 32 20", "", "");
        assertEquals(cm.getSize(), 3);

        Contact contact = cm.getContactByName("Max Didur");
        assertAll(
                () -> assertEquals(contact.getId(), 0),
                () -> assertEquals(contact.getName(), "Max Didur"),
                () -> assertEquals(contact.getPhoneNumber(), "+7 909 908 17 11"),
                () -> assertEquals(contact.getEmail(), "msdidur@edu.hse.ru"),
                () -> assertEquals(contact.getAddess(), "Makovskogo 11")
        );

        Contact contactbyid = cm.getContact(0);
        assertAll(
                () -> assertEquals(contactbyid.getId(), 0),
                () -> assertEquals(contactbyid.getName(), "Max Didur"),
                () -> assertEquals(contactbyid.getPhoneNumber(), "+7 909 908 17 11"),
                () -> assertEquals(contactbyid.getEmail(), "msdidur@edu.hse.ru"),
                () -> assertEquals(contactbyid.getAddess(), "Makovskogo 11")
        );

        Contact contact1 = cm.getContactByName("Pavel Sarmin");
        assertAll(
                () -> assertEquals(contact1.getId(), 1),
                () -> assertEquals(contact1.getName(), "Pavel Sarmin"),
                () -> assertEquals(contact1.getPhoneNumber(), ""),
                () -> assertEquals(contact1.getEmail(), "pfsarmin@edu.hse.ru"),
                () -> assertEquals(contact1.getAddess(), "")
        );

        Contact contact1byid = cm.getContact(1);
        assertAll(
                () -> assertEquals(contact1byid.getId(), 1),
                () -> assertEquals(contact1byid.getName(), "Pavel Sarmin"),
                () -> assertEquals(contact1byid.getPhoneNumber(), ""),
                () -> assertEquals(contact1byid.getEmail(), "pfsarmin@edu.hse.ru"),
                () -> assertEquals(contact1byid.getAddess(), "")
        );

        Contact contact2 = cm.getContactByName("Fedosova Sofya");
        assertAll(
                () -> assertEquals(contact2.getId(), 2),
                () -> assertEquals(contact2.getName(), "Fedosova Sofya"),
                () -> assertEquals(contact2.getPhoneNumber(), "+7 905 809 32 20"),
                () -> assertEquals(contact2.getEmail(), ""),
                () -> assertEquals(contact2.getAddess(), "")
        );

        Contact contact2byid = cm.getContact(2);
        assertAll(
                () -> assertEquals(contact2byid.getId(), 2),
                () -> assertEquals(contact2byid.getName(), "Fedosova Sofya"),
                () -> assertEquals(contact2byid.getPhoneNumber(), "+7 905 809 32 20"),
                () -> assertEquals(contact2byid.getEmail(), ""),
                () -> assertEquals(contact2byid.getAddess(), "")
        );

        // Check get doesn't crash, return null if contact does not exist
        Contact contactnone = cm.getContact(3);
        Contact contactnone1 = cm.getContactByName("Kto Ya");
        assertNull(contactnone);
        assertNull(contactnone1);

        // Check Pages Operations
        cm.setPageSize(2);
        assertEquals(cm.getCurrPage(), 0);
        assertEquals(cm.getPageSize(), 2);
        assertEquals(cm.getNumPages(), 2);
        cm.nextPage();
        assertEquals(cm.getCurrPage(), 1);
        // Check Remove by Id/Name
        cm.removeContactByName("Max Didur");

        // Check if there are no elements on page, the page is removed and currPage scrolled back
        assertEquals(cm.getCurrPage(), 0);
        assertEquals(cm.getNumPages(), 1);

        assertEquals(2, cm.getSize());
        cm.getContact(1);
        cm.removeContact(2);
        assertEquals(1, cm.getSize());
        cm.getContactByName("Pavel Sarmin");

        // Check remove doesn't crash
        cm.removeContact(5);
        cm.removeContactByName("Blessedmane");
        assertEquals(cm.getPageSize(), 1);
    }
}
