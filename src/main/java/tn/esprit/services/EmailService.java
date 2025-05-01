package tn.esprit.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    // Ces propriétés devraient être stockées dans un fichier de configuration sécurisé
    // et non directement dans le code
    private final String username = "loujainrouached1@gmail.com";
    private final String appPassword = "agjn udcb iqbs nmhe"; // Remplacez par votre mot de passe d'application

    public void sendEmail(String to, String subject, String body) {
        sendEmail(to, subject, body, false);
    }

    public void sendEmail(String to, String subject, String body, boolean isHtml) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Artflow"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            if (isHtml) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body);
            }

            Transport.send(message);
            System.out.println("Email envoyé avec succès à: " + to);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendProductApprovalEmail(String to, String productName, String artisanName) {
        String subject = "🎉 Félicitations ! Votre produit a été approuvé";

        String htmlBody =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 2px solid #4CAF50; border-radius: 10px;'>" +
                        "<div style='background-color: #4CAF50; color: white; padding: 15px; text-align: center; border-radius: 5px;'>" +
                        "<h2>🎉 Bonne nouvelle, " + artisanName + " ! 🎉</h2>" +
                        "</div>" +
                        "<div style='padding: 20px; background-color: #f9f9f9; border-radius: 5px; margin-top: 15px;'>" +
                        "<p>Nous sommes ravis de vous informer que votre produit <strong style='color: #4CAF50;'>\"" + productName + "\"</strong> a été approuvé par notre équipe d'administration !</p>" +
                        "<p>Votre création est maintenant disponible sur notre plateforme et visible par tous nos visiteurs. 🚀</p>" +
                        "<p>Nous sommes convaincus que votre savoir-faire unique va apporter une vraie valeur à notre marketplace d'artisans.</p>" +
                        "<hr style='border: 1px dashed #ddd; margin: 20px 0;'>" +
                        "<p>Voici ce qui se passe maintenant :</p>" +
                        "<ul>" +
                        "<li>Votre produit est désormais visible dans notre catalogue en ligne</li>" +
                        "<li>Les clients peuvent commencer à acheter dès maintenant</li>" +
                        "<li>Vous recevrez des notifications pour chaque commande</li>" +
                        "</ul>" +
                        "</div>" +
                        "<div style='margin-top: 20px; padding: 15px; background-color: #f0f7ed; border-left: 4px solid #4CAF50; border-radius: 3px;'>" +
                        "<p>Si vous avez des questions ou besoin d'assistance, notre équipe de support est disponible 24h/24.</p>" +
                        "<p>Contactez-nous par email à <a href='mailto:support@artisans.com' style='color: #4CAF50;'>support@artisans.com</a> ou par téléphone au <strong>+216 54 7412 412</strong>.</p>" +
                        "</div>" +
                        "<div style='text-align: center; margin-top: 25px; color: #777;'>" +
                        "<p>Avec nos sincères félicitations,<br>L'équipe Artisans Marketplace 🧵🔨🎨</p>" +
                        "</div>" +
                        "</div>";

        sendEmail(to, subject, htmlBody, true);
    }

    public void sendProductRejectionEmail(String to, String productName, String artisanName) {
        String subject = "Information concernant votre produit";

        String htmlBody =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 2px solid #FF5722; border-radius: 10px;'>" +
                        "<div style='background-color: #FF5722; color: white; padding: 15px; text-align: center; border-radius: 5px;'>" +
                        "<h2>Information importante, " + artisanName + "</h2>" +
                        "</div>" +
                        "<div style='padding: 20px; background-color: #f9f9f9; border-radius: 5px; margin-top: 15px;'>" +
                        "<p>Nous vous informons que votre produit <strong style='color: #FF5722;'>\"" + productName + "\"</strong> n'a pas été approuvé par notre équipe d'administration.</p>" +
                        "<p>Ne vous découragez pas ! Cela arrive souvent et peut être résolu avec quelques ajustements. 💪</p>" +
                        "<hr style='border: 1px dashed #ddd; margin: 20px 0;'>" +
                        "<p>Voici les raisons possibles :</p>" +
                        "<ul>" +
                        "<li>Photos qui ne mettent pas assez en valeur votre produit</li>" +
                        "<li>Description incomplète ou qui manque de détails</li>" +
                        "<li>Informations de prix ou d'expédition manquantes</li>" +
                        "<li>Le produit ne correspond pas aux catégories de notre marketplace</li>" +
                        "</ul>" +
                        "</div>" +
                        "<div style='margin-top: 20px; padding: 15px; background-color: #fff5f2; border-left: 4px solid #FF5722; border-radius: 3px;'>" +
                        "<p><strong>Notre service de réclamation est disponible 24h/24</strong> pour vous aider à comprendre les raisons précises et vous guider pour soumettre à nouveau votre produit.</p>" +
                        "<p>Contactez-nous par email à <a href='mailto:reclamation@artisans.com' style='color: #FF5722;'>reclamation@artisans.com</a> ou par téléphone au <strong>+216 XX XXX XXX</strong>.</p>" +
                        "</div>" +
                        "<div style='text-align: center; margin-top: 25px; color: #777;'>" +
                        "<p>Nous espérons vous aider à réussir prochainement,<br>L'équipe Artisans Marketplace 🧵🔨🎨</p>" +
                        "</div>" +
                        "</div>";

        sendEmail(to, subject, htmlBody, true);
    }
}