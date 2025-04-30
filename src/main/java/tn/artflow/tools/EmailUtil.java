package tn.artflow.tools;



import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtil {

    private static final String FROM_EMAIL = "skanderselmi19@gmail.com";
    private static final String FROM_PASSWORD = "lism tnzs fqll lfsw";

    public static void sendResetEmail(String toEmail, String token) throws MessagingException {
        String subject = "Reset Your Password";

        String content = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f6f9; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>"
                + "<h2 style='color: #333;'>🔑 Password Reset Request</h2>"
                + "<p style='font-size: 16px; color: #555;'>You requested to reset your password. Please use the following token:</p>"
                + "<div style='text-align: center; margin: 30px 0;'>"
                + "<span style='display: inline-block; background: #f0f0f0; color: #222; padding: 12px 24px; font-size: 20px; font-weight: bold; border-radius: 8px;'>"
                + token
                + "</span>"
                + "</div>"
                + "<p style='font-size: 14px; color: #777;'>Copy this token and paste it in the app to reset your password.</p>"
                + "<hr style='margin: 30px 0;'>"
                + "<p style='font-size: 12px; color: #aaa;'>If you didn't request a password reset, you can ignore this email.</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8"); // <<< IMPORTANT: send HTML

        Transport.send(message);
    }
}
