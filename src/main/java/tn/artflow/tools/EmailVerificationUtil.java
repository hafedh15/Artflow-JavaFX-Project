package tn.artflow.tools;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for managing email verification codes
 * Uses in-memory storage to avoid modifying the database schema
 */
public class EmailVerificationUtil {

    // Storage for verification codes (email -> VerificationData)
    private static final Map<String, VerificationData> verificationCodes = new ConcurrentHashMap<>();

    // Verification code expiration time in minutes
    private static final int EXPIRATION_MINUTES = 15;

    /**
     * Generates a random 6-digit verification code for an email
     * @param email The email to generate a code for
     * @return The generated verification code
     */
    public static String generateVerificationCode(String email) {
        // Generate a random 6-digit code
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 6-digit number between 100000 and 999999
        String verificationCode = String.valueOf(code);

        // Store the code with expiration time
        verificationCodes.put(email, new VerificationData(verificationCode, LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)));

        return verificationCode;
    }

    /**
     * Verifies if a code is valid for an email
     * @param email The email to check
     * @param code The verification code to verify
     * @return true if the code is valid and not expired, false otherwise
     */
    public static boolean verifyCode(String email, String code) {
        VerificationData data = verificationCodes.get(email);

        if (data == null) {
            return false; // No code found for this email
        }

        if (LocalDateTime.now().isAfter(data.expiryTime)) {
            verificationCodes.remove(email); // Clean up expired code
            return false; // Code expired
        }

        if (data.code.equals(code)) {
            verificationCodes.remove(email); // Remove after successful verification
            return true;
        }

        return false; // Invalid code
    }

    /**
     * Clears a verification code for an email
     * @param email The email to clear the code for
     */
    public static void clearVerificationCode(String email) {
        verificationCodes.remove(email);
    }

    /**
     * Check if an email has a valid verification code
     * @param email The email to check
     * @return true if there's a non-expired code for the email
     */
    public static boolean hasValidVerificationCode(String email) {
        VerificationData data = verificationCodes.get(email);

        if (data == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(data.expiryTime)) {
            verificationCodes.remove(email); // Clean up expired code
            return false;
        }

        return true;
    }

    /**
     * Class to store verification data with expiration time
     */
    private static class VerificationData {
        private final String code;
        private final LocalDateTime expiryTime;

        public VerificationData(String code, LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
    }
}