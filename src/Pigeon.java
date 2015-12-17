
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
//import javax.activation.*;

public class Pigeon {
final static String username = "fernandolopez1991@gmail.com";
final static String password = "Manu0120";
	
	
	public static String emailer (String to, String subject, String mess)
	{
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication() 
			{
				return new PasswordAuthentication(username, password);
			}
		});
		
		try
		{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("fernandolopez1991@gmail.com"));
			message.addRecipients(Message.RecipientType.CC,InternetAddress.parse( to));
			message.setSubject(subject);
			message.setText(mess);
			Transport.send(message);
			return "done";
		}
		catch (MessagingException e)
		{
			
		
			return e.toString();
		}
	}
}
