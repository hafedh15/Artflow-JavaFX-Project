package tn.artflow.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.mail.Message;
import jakarta.mail.Multipart;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.services.ReservationService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.UUID;

public class RegisterReservation {

    @FXML
    private TextField seatsField;

    @FXML
    private ImageView imageView;

    @FXML
    private TextArea notesArea;
    @FXML
    private Label seatsErrorLabel;

    @FXML
    private Label notesErrorLabel;


    @FXML
    private Label workshopTitle;

    @FXML
    private Label workshopDate;

    @FXML
    private Label workshopLocation;

    @FXML
    private Label workshopType;
    @FXML
    private Label workshopDescription;

    @FXML
    private ImageView workshopImage;

    private Workshop workshop;

    public void setWorkshop(Workshop w) {
        this.workshop = w;

        workshopTitle.setText("📚 " + w.getTitle());
        workshopDescription.setText("📚 " + w.getDescription());
        workshopDate.setText("📅 " + w.getDate());
        workshopLocation.setText("📍 " + w.getLocation());
        workshopType.setText("🧾 Type: " + w.getType());

        String imageName = w.getImage(); // Ex: "/images/workshops/example.jpg"
        String imageFullPath = "C:/xampp/htdocs" + imageName;

        File imageFile = new File(imageFullPath);
        Image image = imageFile.exists()
                ? new Image(imageFile.toURI().toString())
                : new Image(getClass().getResourceAsStream("/images/default-workshop.png"));

        imageView.setImage(image);

    }

    @FXML
    private void handleSubmit() {
        seatsErrorLabel.setText("");
        notesErrorLabel.setText("");

        boolean isValid = true;
        String seatText = seatsField.getText().trim();
        int seats = 0;

        try {
            seats = Integer.parseInt(seatText);
            if (seats < 1 || seats > 4) {
                seatsErrorLabel.setText("You can reserve 1 to 4 seats only.");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            seatsErrorLabel.setText("Please enter a valid number.");
            isValid = false;
        }

        String notes = notesArea.getText().trim();
        if (notes.length() > 20) {
            notesErrorLabel.setText("Notes cannot exceed 20 characters.");
            isValid = false;
        }

        if (!isValid) return;

        try {
            String date = LocalDate.now().toString();
            String code = "RES-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

            Reservation reservation = new Reservation();
            reservation.setSeatsReserved(seats);
            reservation.setNotes(notes);
            reservation.setDateReservation(date);
            reservation.setUniqueCode(code);
            reservation.setWorkshop(workshop);

            User currentUser = tn.artflow.utils.UserSession.getInstance().getUser();
            reservation.setUser(currentUser);
            
            new ReservationService().ajouter(reservation);

            // ✅ Générer le QR Code
            generateQRCode(code, "qrcode.png");

            // ✅ Envoyer l’e-mail
            sendEmailWithQRCode(currentUser.getEmail(), code, "qrcode.png");

            System.out.println("✅ Reservation successful!");
            Stage stage = (Stage) seatsField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            seatsErrorLabel.setText("Something went wrong. Try again.");
        }
    }

    private void generateQRCode(String data, String filePath) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
        Path path = Paths.get(filePath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
    }


    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void sendEmailWithQRCode(String toEmail, String code, String filePath) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("skanderselmi19@gmail.com", "gdowzlthlmrmubtg");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("skanderselmi19@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("🎨 Confirmation de réservation");

        // 🔵 Le corps HTML avec l'image au milieu
        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px; text-align: center;'>" +
                "<h2 style='color: #2e86de;'>Thank you for your reservation 🎉</h2>" +
                "<p>Hello,</p>" +
                "<p>Your reservation has been successfully confirmed.</p>" +
                "<p><strong>Your reservation code:</strong><br><span style='color: #27ae60; font-size: 20px;'>" + code + "</span></p>" +
                "<br>" +
                "<img src='cid:qrcodeImage' style='width:300px; height:300px; margin-top:20px;'/>" +
                "<br><p style='font-size:12px;color:gray;'>Thank you for choosing ArtFlow ✨</p>" +
                "</body></html>";

        // 🔵 Partie HTML
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlMessage, "text/html; charset=UTF-8");

        // 🔵 Partie image QR code
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.attachFile(new File(filePath));
        imagePart.setContentID("<qrcodeImage>");
        imagePart.setDisposition(MimeBodyPart.INLINE); // Important pour afficher l'image dans l'email

        // 🔵 Regrouper tout
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);
        multipart.addBodyPart(imagePart);

        message.setContent(multipart);

        // 🚀 Envoi
        Transport.send(message);
    }


    @FXML
    void cancelUpdate(ActionEvent event) {
        // Close the window
        Stage stage = (Stage) notesArea.getScene().getWindow();
        stage.close();
    }


}