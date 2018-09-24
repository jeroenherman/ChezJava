package be.leerstad.chezjava.reports;

import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


public class Email {

    private static Logger logger = Logger.getLogger(Email.class);
    private static boolean isEmailSend = false;
    private final String workingDir = System.getProperty("user.dir") + "/src/main/resources/";
    private File file;
    private String fileName;

    /**
     * Constructor
     *
     * @param file
     * @param fileName
     */
    public Email(File file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    public static boolean isEmailSend() {
        return isEmailSend;
    }

    public void sendMail() {

        isEmailSend = false;
        final String userName;
        final String password;
        final String from;
        final String to;
        final String propertiesName = "mail.properties";
        Properties props = new Properties();
        InputStream input;

        try {
            input = new FileInputStream(workingDir + propertiesName);
            props.load(input);
            input.close();
        } catch (Exception e) {
            logger.error(e);
        }

        userName = props.getProperty("mail.smtp.user");
        password = props.getProperty("mail.smtp.password");
        from = props.getProperty("mail.smtp.user");
        to = props.getProperty("Receiver");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, password);
                    }
                });

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Report: " + fileName);
            logger.debug("FileName:" + fileName);
            logger.debug(" Send: " + fileName + "from: " + from + "-> to: " + to);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Dear  \n\nIn attachment you'll find the requested report  \n\nKind Regards \n\nChez  Java ");

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            String attachment = file.getAbsolutePath();
            DataSource source = new FileDataSource(attachment);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName + ".pdf");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport transport = session.getTransport("smtp");
            transport.connect(userName, password);
            transport.send(message);
            isEmailSend = true;
            transport.close();

        } catch (MessagingException mex) {
            logger.error(mex.getMessage(), mex);
        }
    }

}
