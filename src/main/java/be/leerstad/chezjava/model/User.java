package be.leerstad.chezjava.model;

import be.leerstad.chezjava.database.DAOWaiterMySql;

import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = -7626334149180637289L;
    private String firstName;
    private String lastName;
    private String password;

    public User(String firstName, String lastName, String password) {
        this.firstName = cleanName(firstName);
        this.lastName = cleanName(lastName);
        this.password = password;
    }

    //
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!getFirstName().equals(user.getFirstName())) return false;
        if (!getLastName().equals(user.getLastName())) return false;
        return getPassword().equals(user.getPassword());
    }

    @Override
    public int hashCode() {
        int result = getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        result = 31 * result + getPassword().hashCode();
        return result;
    }

    public int getUserID() {
        int userId = DAOWaiterMySql.getInstance().getWaiterId(firstName, lastName);
        return userId;
    }

    private String cleanName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }
}
