package com.ies.schoolos.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.ies.schoolos.container.Container;
import com.ies.schoolos.schema.SessionSchema;
import com.ies.schoolos.schema.UserSchema;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class EmailSender {
	private Container container = new Container();
	
	public EmailSender(String to, String subject, String description, String filename, InputStream inputStream) {
		final String username = "administrator@schoolos.in.th";
		final String password = "!IeSP@ssw0RD?";
		
	    // Sender's email ID needs to be mentioned
	    String from = "";
	    if(SessionSchema.getEmail() == null){
	    	StringBuilder builder = new StringBuilder();
	    	builder.append(" SELECT * FROM " + UserSchema.TABLE_NAME);
	    	builder.append(" WHERE " + UserSchema.REF_USER_ID + "=" + SessionSchema.getSchoolID());
	    	
	    	SQLContainer userContainer = container.getFreeFormContainer(builder.toString(), UserSchema.USER_ID);
	    	Item userItem = userContainer.getItem(userContainer.getIdByIndex(0));
	    	
	    	from = userItem.getItemProperty(UserSchema.EMAIL).getValue().toString();

	    }else{
	    	from = SessionSchema.getEmail().toString();
	    }

	    // Get system properties
	    Properties properties = System.getProperties();
	
	    // Setup mail server
	    //properties.setProperty("mail.smtp.host", host);
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.smtp.starttls.enable", "true");
	    properties.put("mail.smtp.host", "smtp.gmail.com");
	    properties.put("mail.smtp.port", "587");
	    
	    // Get the default Session object.
	    Session session = Session.getInstance(properties,
			  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
		});
	    
	    try{
	       // Create a default MimeMessage object.
	       MimeMessage message = new MimeMessage(session);
	
	       // Set From: header field of the header.
	       message.setFrom(new InternetAddress(from));
	
	       // Set To: header field of the header.
	       message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
	
	       // Set Subject: header field
	       message.setSubject(subject);
	
	       // Create the message part 
	       BodyPart messageBodyPart = new MimeBodyPart();
	
	       // Fill the message
	       messageBodyPart.setText(description);
	       
	       // Create a multipar message
	       Multipart multipart = new MimeMultipart();
	
	       // Set text message part
	       multipart.addBodyPart(messageBodyPart);
	
	       if(filename != null || inputStream != null){
	    	   // Part two is attachment
		       messageBodyPart = new MimeBodyPart();
		
		       ByteArrayDataSource ds = new ByteArrayDataSource(inputStream, "application/pdf"); 
		       
		       messageBodyPart.setDataHandler(new DataHandler(ds));
		       messageBodyPart.setFileName(filename);
		       multipart.addBodyPart(messageBodyPart);
	       }
	      
	
	       // Send the complete message parts
	       message.setContent(multipart );
	
	       // Send message
	       Transport.send(message);
	
	    }catch (MessagingException mex) {
	       mex.printStackTrace();
	    } catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
