package be.leerstad.chezjava.reports;

import org.apache.log4j.Logger;

public class EmailThread extends Thread {
    Logger logger = Logger.getLogger(EmailThread.class.getName());
    private Email email;

    public EmailThread(Email email) {
        super();
        this.email = email;
    }

    @Override
    public void run() {
        try {
            email.sendMail();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
