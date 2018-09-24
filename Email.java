package be.leerstad.cafe.model;

import org.apache.log4j.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;



public class Email {
	
	private static Logger log = Logger.getLogger (Email.class);
	

	public boolean sendMail (String file, String fileName) {
		
		boolean sent = false;
		final String userName;
		final String password;
		final String from;
		final String to;
		final String propertiesName = "mail.properties";
		Properties props = new Properties ();
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream (propertiesName)) {
			
			props.load (inputStream);
			
		} catch (Exception e) {
			log.error (e);
		}
		
		userName = props.getProperty ("mail.smtp.user");
		password = props.getProperty ("mail.smtp.password");
		from = props.getProperty ("mail.smtp.user");
		to = props.getProperty ("Receiver");
		
		Session session = Session.getInstance (props,
				new javax.mail.Authenticator () {
					protected javax.mail.PasswordAuthentication getPasswordAuthentication () {
						return new javax.mail.PasswordAuthentication (userName, password);
					}
				});
		
		try {
			
			MimeMessage message = new MimeMessage (session);
			message.setFrom (new InternetAddress (from));
			message.addRecipient (Message.RecipientType.TO, new InternetAddress (to));
			message.setSubject ("Report " + fileName);
			
			BodyPart messageBodyPart = new MimeBodyPart ();
			messageBodyPart.setText ("Dear  \n\nIn attachment you'll find the requested report  \n\nKind Regards \n\nPeter Hardeel ");
			
			Multipart multipart = new MimeMultipart ();
			
			multipart.addBodyPart (messageBodyPart);
			
			messageBodyPart = new MimeBodyPart ();
			String attachment = file;
			DataSource source = new FileDataSource (attachment);
			messageBodyPart.setDataHandler (new DataHandler (source));
			messageBodyPart.setFileName (attachment);
			multipart.addBodyPart (messageBodyPart);
			
			message.setContent (multipart);
			
			Transport transport = session.getTransport ("smtp");
			transport.connect (userName, password);
			transport.send (message);
			transport.close ();
			sent = true;
			
		} catch (MessagingException mex) {
			log.error (mex);
		}
		return sent;
	}
	
}
