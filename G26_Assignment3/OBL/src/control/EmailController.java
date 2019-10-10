package control;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class EmailController {
	
	private static EmailController singleton = null;

	private EmailController() {	}
	
	public synchronized static EmailController getInstance() {
		if(singleton == null)
			singleton = new EmailController();
		return singleton;
	}
	
	
	public void sendVerficationMail(String userEmail, String emailSubject, String emailMsg) {
		try {

			String host = "imap.gmail.com";
			String user = "g26.obl@gmail.com";
			String pass = "G26Aa123456";
			String to = userEmail;
			String from = "g26.obl@gmail.com";
			String subject = emailSubject;
			String messageText = emailMsg;
			boolean sessionDebug = false;

			Properties props = System.getProperties();

			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.required", "true");

			java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			Session mailSession = Session.getDefaultInstance(props, null);
			mailSession.setDebug(sessionDebug);
			Message msg = new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(messageText);

			Transport transport = mailSession.getTransport("smtp");
			transport.connect(host, user, pass);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			//System.out.println("message send successfully");
		} catch (Exception ex) {
			System.out.println(ex);
		}
		 
	}
}
