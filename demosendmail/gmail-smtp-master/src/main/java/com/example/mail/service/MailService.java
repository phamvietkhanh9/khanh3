package com.example.mail.service;

import com.example.mail.mail.MailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Service
public class MailService {
    private final MailProperties mailProperties;

    @Autowired
    public MailService(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    public boolean sendVerificationMail(String toMail, String verificationCode) {
        String subject = "Verify your email";
        String body = "<a href='http://localhost:4000/verified?code=" + verificationCode + "'>Click to verify</a>";

        return sendMail(toMail, subject, body);
    }

    private boolean sendMail(String toMail, String subject, String body) {
        try {
            System.out.println(mailProperties.getFrom());
            System.out.println(mailProperties.getPort());
            Properties properties = System.getProperties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.port", mailProperties.getPort());
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProperties.getFrom(), mailProperties.getFromName()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
            message.setSubject(subject);
            message.setContent(body, "text/html");

            Transport transport = session.getTransport();
            transport.connect(mailProperties.getHost(), mailProperties.getUsername(), mailProperties.getPassword());

            transport.sendMessage(message, message.getAllRecipients());

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }
}
