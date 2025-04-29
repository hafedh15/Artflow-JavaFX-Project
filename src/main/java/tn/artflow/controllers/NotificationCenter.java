package tn.artflow.controllers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationCenter {
    private static final String FILE_PATH = "notifications.txt";
    private static final List<String> notifications = new ArrayList<>();

    static {
        // Charger les notifications existantes depuis le fichier au démarrage
        loadNotificationsFromFile();
    }

    public static void addNotification(String notification) {
        notifications.add(notification);
        saveNotificationToFile(notification);
    }

    public static List<String> getNotifications() {
        return new ArrayList<>(notifications); // retourne une copie sécurisée
    }

    public static void clearNotifications() {
        notifications.clear();
        clearNotificationFile();
    }

    private static void saveNotificationToFile(String notification) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(notification);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadNotificationsFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                notifications.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void clearNotificationFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // écrire rien = vider le fichier
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
