package tn.artflow.controllers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationCenter {

    private static final String FILE_PATH = "notifications.txt";
    private static final List<String> notifications = new ArrayList<>();

    static {
        loadNotificationsFromFile();
    }

    public static void addNotification(String notification) {
        notifications.add(notification);
        appendNotificationToFile(notification);
    }

    public static List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public static void clearNotifications() {
        notifications.clear();
        clearNotificationsFile();
    }

    private static void appendNotificationToFile(String notification) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(notification);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier de notifications : " + e.getMessage());
        }
    }

    private static void loadNotificationsFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        notifications.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                notifications.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier de notifications : " + e.getMessage());
        }
    }

    private static void clearNotificationsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Vide le fichier en l'écrasant
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du contenu du fichier de notifications : " + e.getMessage());
        }
    }
}
