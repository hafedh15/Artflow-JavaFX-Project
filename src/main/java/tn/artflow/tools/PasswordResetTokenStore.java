package tn.artflow.tools;

import java.util.HashMap;
import java.util.Map;

public class PasswordResetTokenStore {

    private static final Map<String, String> tokenMap = new HashMap<>();

    public static void storeToken(String email, String token) {
        tokenMap.put(token, email);
    }

    public static String getEmailByToken(String token) {
        return tokenMap.get(token);
    }

    public static void removeToken(String token) {
        tokenMap.remove(token);
    }
}
