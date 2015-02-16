package tn;

/* based on http://sanjaal.com/java/tag/java-internet-address-example/ */

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;

public class SendBCCMail {

    static final String SMTP_HOST = "smtp.gmail.com";
    static final int SMTP_HOST_PORT = 465;
    static final String SMTP_AUTH_USER = "yourName@gmail.com";
    static final String SMTP_AUTH_PWD = "yourPassword";

    public static void main(String[] args) {
        String subject = "Find the file attached";

        try {
            new SendBCCMail().sendEmail(subject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(String subject) throws Exception {
        /* Setting the mail properties */
        Properties props = new Properties();
        props.put("mail.smtps.host", SMTP_HOST);
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.auth", "true");
        /**
         * setting quitwait to false causes the SMTP server to close the
         * connection immediately after the message is sent
         */
        props.put("mail.smtps.quitwait", "false");

        Session mailSession = Session.getDefaultInstance(props);

        mailSession.setDebug(true);

        MimeMessage message = new MimeMessage(mailSession);

        message.setSubject(subject);

        Address bccAddress1 = new InternetAddress("address1@x.com");
        Address bccAddress2 = new InternetAddress("address2@y.com");
        Address bccAddress3 = new InternetAddress("address3@z.com");
        Address[] addressList = new Address[] { bccAddress1, bccAddress2,
                bccAddress3 };

        /* omitting receiver TO prevents removes its visibility and works for
         * gmail.com, hushmail.com and outlook.com at least - that is all I have
         * tested
         * 
         * Adding a receiver TO /*
         * message.addRecipient(Message.RecipientType.TO, toAddress);
         */

        /* Adding an array of Recipients for BCC */
        message.addRecipients(Message.RecipientType.BCC, addressList);

        BodyPart messageBodyPart = new MimeBodyPart();

        /* Defining a message text. This is the main body of the email message */
        messageBodyPart.setText("Please find the file attached.\n");
        MimeMultipart mmultipart = new MimeMultipart();
        mmultipart.addBodyPart(messageBodyPart);

        /* Preparing an attachment */
        MimeBodyPart mimeBodyPartAttachmet = addAttachment("letter-C.gif");
        mmultipart.addBodyPart(mimeBodyPartAttachmet);
        message.setContent(mmultipart, "multipart/mixed");// attachment

        Transport transport = mailSession.getTransport();
        transport.connect(SMTP_HOST, SMTP_HOST_PORT, SMTP_AUTH_USER,
                SMTP_AUTH_PWD);

        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    /* Creates and sends a MimeBodyPart object for attachment to email */
    public static MimeBodyPart addAttachment(String fileNameToAttach) {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        FileDataSource fileDataSource = new FileDataSource("file.txt");
        try {
            mimeBodyPart.setDataHandler(new DataHandler(fileDataSource));
            mimeBodyPart.setFileName(fileNameToAttach);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mimeBodyPart;
    }
}
