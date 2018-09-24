package be.leerstad.chezjava.model;

import org.apache.log4j.Logger;

import java.io.Serializable;

public class Waiter extends User implements Serializable, Comparable<Waiter> {
    private static final long serialVersionUID = -363329686044477602L;
    private static Logger logger = Logger.getLogger(Waiter.class.getName());

    public Waiter(String firstName, String lastName, String password) {

        super(firstName, lastName, password);
        logger.debug("Waiter Created:" + this.toString());
    }

    //
    public String getFirstName() {
        return super.getFirstName();
    }

    public String getLastName() {
        return super.getLastName();
    }

    public String getPassword() {
        return super.getPassword();
    }
    //

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getFirstName()).append(" ").append(getLastName());
        return sb.toString();
    }

    @Override
    public int compareTo(Waiter o) {
        if (!(this.getFirstName().equals(o.getFirstName())))
            return this.getFirstName().compareTo(o.getFirstName());

        if (!(this.getLastName().equals(o.getLastName())))
            return this.getFirstName().compareTo(o.getFirstName());
        return this.getPassword().compareTo(o.getPassword());
    }
}
