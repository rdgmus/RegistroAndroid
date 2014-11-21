package it.keyorchestra.registrowebapp.scuola.util;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SimpleMail2 {
	public void send(String emailTo, String subject, String body, 
			String imgPath, String contentId) {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", "smtp.mymailserver.com");
		props.setProperty("mail.user", "keyorchestra2014@gmail.com");
		props.setProperty("mail.password", "diavgek@");

		Session mailSession = Session.getDefaultInstance(props, null);
		mailSession.setDebug(true);
		try {
			Transport transport = mailSession.getTransport();

			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			message.setFrom(new InternetAddress("keyorchestra2014@gmail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					emailTo));

			//
			// This HTML mail have to 2 part, the BODY and the embedded image
			//
			MimeMultipart multipart = new MimeMultipart("related");

			// first part (the html)
			BodyPart messageBodyPart = new MimeBodyPart();
			String htmlText = body;
			
			messageBodyPart.setContent(htmlText, "text/html");

			// add it
			multipart.addBodyPart(messageBodyPart);

			// second part (the image)
			messageBodyPart = new MimeBodyPart();
			DataSource fds = new FileDataSource(imgPath);
			messageBodyPart.setDataHandler(new DataHandler(fds));
			messageBodyPart.setHeader("Content-ID", contentId);

			// add it
			multipart.addBodyPart(messageBodyPart);

			// put everything together
			message.setContent(multipart);

			transport.connect();
			transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
			transport.close();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
