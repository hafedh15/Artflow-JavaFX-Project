package tn.artflow.tools;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Utility class for generating avatar images.
 * Offers different avatar generation methods.
 */
public class AvatarGenerator {

    private static final String[] COLORS = {
            "5C4F3D", // Primary theme color (dark brown)
            "8A7967", // Lighter brown
            "A69781", // Taupe
            "C3B6A5", // Beige
            "E8DECD", // Background color (light beige)
            "D1C4AF", // Darker beige
            "7D6E58", // Medium brown
            "B49F7E"  // Sandy brown
    };

    /**
     * Get a random color from the predefined array
     */
    private static String getRandomColor() {
        Random random = new Random();
        return COLORS[random.nextInt(COLORS.length)];
    }

    /**
     * Generate an avatar based on initials with a random background color
     * @param name The name to use for initials
     * @param size The size of the avatar image (width and height)
     * @return The generated avatar Image
     */
    public static Image generateInitialsAvatar(String name, int size) {
        try {
            String initials = getInitials(name);
            String backgroundColor = getRandomColor();
            String urlString = String.format(
                    "https://ui-avatars.com/api/?name=%s&size=%d&background=%s&color=fff&bold=true",
                    URLEncoder.encode(initials, StandardCharsets.UTF_8.toString()),
                    size,
                    backgroundColor
            );

            URL url = new URL(urlString);
            return new Image(url.toString());
        } catch (Exception e) {
            System.err.println("Error generating initials avatar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate an avatar based on the user's email (uses Gravatar)
     * @param email The email to use
     * @param size The size of the avatar image (width and height)
     * @return The generated avatar Image
     */
    public static Image generateGravatarAvatar(String email, int size) {
        try {
            // Create MD5 hash of email (Gravatar requirement)
            String emailHash = MD5Util.md5Hex(email.trim().toLowerCase());
            String urlString = String.format(
                    "https://www.gravatar.com/avatar/%s?s=%d&d=identicon",
                    emailHash, size
            );

            URL url = new URL(urlString);
            return new Image(url.toString());
        } catch (Exception e) {
            System.err.println("Error generating Gravatar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a unique abstract avatar (uses DiceBear)
     * @param seed A seed string to ensure consistent generation for the same user
     * @param size The size of the avatar image (width and height)
     * @return The generated avatar Image
     */
    public static Image generateAbstractAvatar(String seed, int size) {
        try {
            // Use DiceBear Avatars API with identicon style, but request PNG instead of SVG
            String encodedSeed = URLEncoder.encode(seed, StandardCharsets.UTF_8.toString());
            String urlString = String.format(
                    "https://api.dicebear.com/9.x/identicon/png?seed=%s&size=%d&backgroundColor=%s",
                    encodedSeed, size, getRandomColor()
            );

            System.out.println("Abstract URL: " + urlString); // Debug

            URL url = new URL(urlString);
            return new Image(url.toString());
        } catch (Exception e) {
            System.err.println("Error generating abstract avatar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate pixel art avatar (uses DiceBear pixel art)
     * @param seed A seed string to ensure consistent generation for the same user
     * @param size The size of the avatar image (width and height)
     * @return The generated avatar Image
     */
    public static Image generatePixelArtAvatar(String seed, int size) {
        try {
            // Use DiceBear Avatars API with pixel art style, but request PNG instead of SVG
            String encodedSeed = URLEncoder.encode(seed, StandardCharsets.UTF_8.toString());
            String urlString = String.format(
                    "https://api.dicebear.com/9.x/pixel-art/png?seed=%s&size=%d",
                    encodedSeed, size
            );

            System.out.println("Pixel Art URL: " + urlString); // Debug

            URL url = new URL(urlString);
            return new Image(url.toString());
        } catch (Exception e) {
            System.err.println("Error generating pixel art avatar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a character-style avatar with face and shoulders using DiceBear Adventurer
     * @param seed A seed string to ensure consistent generation for the same user
     * @param size The size of the avatar image (width and height)
     * @return The generated avatar Image
     */
    public static Image generateCharacterAvatar(String seed, int size) {
        try {
            // Use DiceBear Avatars API with adventurer style, but request PNG instead of SVG
            String encodedSeed = URLEncoder.encode(seed, StandardCharsets.UTF_8.toString());
            String urlString = String.format(
                    "https://api.dicebear.com/9.x/adventurer/png?seed=%s&size=%d",
                    encodedSeed, size
            );

            System.out.println("Character URL: " + urlString); // Debug

            URL url = new URL(urlString);
            return new Image(url.toString());
        } catch (Exception e) {
            System.err.println("Error generating character avatar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a cartoon-like avatar with face and full body using DiceBear Avataaars
     * @param seed A seed string to ensure consistent generation for the same user
     * @param size The size of the avatar image (width and height)
     * @return The generated avatar Image
     */
    public static Image generateFullBodyAvatar(String seed, int size) {
        try {
            // Use DiceBear Avatars API with avataaars style, but request PNG instead of SVG
            String encodedSeed = URLEncoder.encode(seed, StandardCharsets.UTF_8.toString());
            String urlString = String.format(
                    "https://api.dicebear.com/9.x/avataaars/png?seed=%s&size=%d",
                    encodedSeed, size
            );

            System.out.println("Full Body URL: " + urlString); // Debug

            URL url = new URL(urlString);
            return new Image(url.toString());
        } catch (Exception e) {
            System.err.println("Error generating full body avatar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get initials from a name, up to 2 characters
     */
    private static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }

        // Split the name by spaces
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        // Add first letter of first name
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials.append(parts[0].charAt(0));
        }

        // Add first letter of last name (if exists)
        if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
            initials.append(parts[parts.length - 1].charAt(0));
        }

        return initials.toString().toUpperCase();
    }
}
/**
 * Utility class to generate MD5 hashes for Gravatar
 */
class MD5Util {
    public static String md5Hex(String message) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}