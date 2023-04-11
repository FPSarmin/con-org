package Manager.Contact;

import org.apache.commons.lang3.StringUtils;

public class Contact {
    private int id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;

    public Contact(int id, String name, String phoneNumber, String email, String address) throws IllegalArgumentException {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddess() {
        return address;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void display() {
        System.out.println(StringUtils.repeat("#", 70));
        System.out.printf("## Id           :%s%s ##\n", StringUtils.repeat(' ', 50-Integer.toString(id).length()), Integer.toString(id));
        System.out.printf("## Name         :%s%s ##\n", StringUtils.repeat(' ', 50-name.length()), name);
        System.out.printf("## Phone Number :%s%s ##\n", StringUtils.repeat(' ', 50-phoneNumber.length()), phoneNumber);
        System.out.printf("## Email        :%s%s ##\n", StringUtils.repeat(' ', 50-email.length()), email);
        System.out.printf("## Address      :%s%s ##\n", StringUtils.repeat(' ', 50-address.length()), address);
        System.out.println(StringUtils.repeat("#", 70));
    }
}
