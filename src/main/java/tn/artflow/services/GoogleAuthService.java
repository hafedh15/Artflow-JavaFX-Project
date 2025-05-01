package tn.artflow.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.tools.CallbackServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class GoogleAuthService {

    // Replace these values with those obtained from Google Cloud Console
    private static final String CLIENT_ID = "659352778662-p3i674ucgu25tv0ab0v2l1ovqt0jrtt5.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";

    // Base redirect URI - the actual port will be determined at runtime
    private static final String REDIRECT_URI_BASE = "http://localhost:%d/callback";

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService;

    public GoogleAuthService() {
        this.userService = new UserService();
    }

    public CompletableFuture<User> startGoogleAuth() {
        CompletableFuture<User> future = new CompletableFuture<>();

        try {
            // Start the callback server
            CallbackServer callbackServer = new CallbackServer();
            callbackServer.start();

            // Get the actual port used
            int port = callbackServer.getPort();
            String redirectUri = String.format(REDIRECT_URI_BASE, port);

            System.out.println("Using redirect URI: " + redirectUri);

            // Create WebView to display Google login page
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            // Create a window for the WebView
            Stage authStage = new Stage();
            authStage.setTitle("Sign in with Google");
            authStage.setScene(new Scene(webView, 650, 700));

            // Build the auth URL with the correct redirect URI
            String authUrl = buildAuthURL(redirectUri);
            System.out.println("Loading auth URL: " + authUrl);

            // Load the authentication page
            webEngine.load(authUrl);

            // Add logging to track page loads
            webEngine.setOnStatusChanged(event ->
                    System.out.println("WebEngine status: " + event.toString()));

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) ->
                    System.out.println("WebEngine state: " + newValue));

            authStage.show();

            // Wait for the authorization code from the callback server
            callbackServer.getAuthCodeFuture()
                    .thenCompose(authCode -> {
                        System.out.println("Auth code received: " + authCode.substring(0, 5) + "...");

                        // Close the authentication window
                        Platform.runLater(() -> authStage.close());

                        return CompletableFuture.supplyAsync(() -> {
                            try {
                                // Exchange the code for an access token
                                String tokenJson = exchangeCodeForToken(authCode, redirectUri);
                                JsonNode tokenData = mapper.readTree(tokenJson);

                                if (!tokenData.has("access_token")) {
                                    throw new IOException("Access token not found in response: " + tokenJson);
                                }

                                String accessToken = tokenData.get("access_token").asText();
                                System.out.println("Access token received: " + accessToken.substring(0, 10) + "...");

                                // Get user information
                                String userInfoJson = getUserInfo(accessToken);
                                System.out.println("User info received: " + userInfoJson);

                                User googleUser = parseUserInfo(userInfoJson);
                                System.out.println("Parsed user: " + googleUser.getEmail());

                                // Mark the user as coming from Google
                                googleUser.setGoogleAccount(true);

                                // Check if the user already exists
                                User existingUser = null;
                                try {
                                    existingUser = userService.findByEmail(googleUser.getEmail());
                                    System.out.println("Existing user found: " + (existingUser != null));
                                } catch (SQLException e) {
                                    throw new RuntimeException("Database error: " + e.getMessage(), e);
                                }

                                final User finalUser;
                                if (existingUser != null) {
                                    // Existing user
                                    finalUser = existingUser;
                                } else {
                                    // New user
                                    googleUser.setIsVerified(true); // Already verified by Google
                                    googleUser.setRoles("[\"ROLE_CLIENT\"]"); // Default role
                                    googleUser.setPassword(""); // No local password
                                    googleUser.setDateCreation(new Date()); // Set creation date
                                    googleUser.setIs_Banned(false); // Not banned by default

                                    try {
                                        userService.ajouter(googleUser);
                                        finalUser = googleUser;
                                        System.out.println("New user created: " + finalUser.getEmail());
                                    } catch (SQLException e) {
                                        throw new RuntimeException("Error adding user: " + e.getMessage(), e);
                                    }
                                }

                                return finalUser;
                            } catch (Exception e) {
                                System.err.println("Error during authentication: " + e.getMessage());
                                e.printStackTrace();
                                throw new RuntimeException("Error during authentication: " + e.getMessage(), e);
                            }
                        });
                    })
                    .thenAccept(user -> {
                        System.out.println("Authentication completed for: " + user.getEmail());
                        Platform.runLater(() -> future.complete(user));
                    })
                    .exceptionally(ex -> {
                        System.err.println("Authentication failed: " + ex.getMessage());
                        ex.printStackTrace();
                        Platform.runLater(() -> future.completeExceptionally(ex));
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("Error starting Google auth: " + e.getMessage());
            e.printStackTrace();
            future.completeExceptionally(e);
        }

        return future;
    }

    private String buildAuthURL(String redirectUri) {
        try {
            return AUTH_URL + "?" +
                    "client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8") + "&" +
                    "redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") + "&" +
                    "response_type=code&" +
                    "scope=" + URLEncoder.encode("email profile", "UTF-8") + "&" +
                    "access_type=offline&" +
                    "prompt=consent";  // Always show the consent screen
        } catch (Exception e) {
            throw new RuntimeException("Error building authentication URL", e);
        }
    }

    private String exchangeCodeForToken(String code, String redirectUri) throws IOException {
        URL url = new URL(TOKEN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        String postData = "code=" + URLEncoder.encode(code, "UTF-8") +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8") +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, "UTF-8") +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&grant_type=authorization_code";

        // Print for debugging
        System.out.println("Token request: " + postData);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();

        // Print for debugging
        System.out.println("Response code: " + responseCode + " - " + responseMessage);

        if (responseCode != 200) {
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
            }
            System.out.println("Complete error: " + errorResponse);
            throw new IOException("HTTP Error: " + responseCode + " - " + errorResponse);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private String getUserInfo(String accessToken) throws IOException {
        URL url = new URL(USERINFO_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
            }
            throw new IOException("HTTP Error when retrieving user info: " + responseCode + " - " + errorResponse);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private User parseUserInfo(String json) throws IOException {
        JsonNode userInfo = mapper.readTree(json);

        User user = new User();
        user.setEmail(userInfo.has("email") ? userInfo.get("email").asText() : "");

        // Note: Google user info typically returns given_name and family_name fields
        // Check both formats to ensure compatibility
        if (userInfo.has("given_name")) {
            user.setLastname(userInfo.get("given_name").asText());
        } else if (userInfo.has("givenName")) {
            user.setLastname(userInfo.get("givenName").asText());
        } else {
            user.setLastname("");
        }

        if (userInfo.has("family_name")) {
            user.setName(userInfo.get("family_name").asText());
        } else if (userInfo.has("familyName")) {
            user.setName(userInfo.get("familyName").asText());
        } else {
            user.setName("");
        }

        // Get profile picture if available
        if (userInfo.has("picture")) {
            user.setPhoto(userInfo.get("picture").asText());
        }

        return user;
    }
}