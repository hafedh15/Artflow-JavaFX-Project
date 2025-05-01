package tn.artflow.entities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GoogleSignIn {
    private static final String CLIENT_SECRETS_FILE = "/client_secrets.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email"
    );
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static HttpTransport httpTransport;
    private static GoogleClientSecrets clientSecrets;
    private static GoogleAuthorizationCodeFlow flow;
    private static CompletableFuture<String> authorizationCodeFuture;

    static {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            // Load client secrets
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(GoogleSignIn.class.getResourceAsStream(CLIENT_SECRETS_FILE), StandardCharsets.UTF_8));


            // Build flow and trigger user authorization request
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Error initializing Google Sign-In: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Credential authorize() throws Exception {
        authorizationCodeFuture = new CompletableFuture<>();

        String authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0))
                .build();

        // Open browser for authorization
        openBrowser(authorizationUrl);

        // Wait for authorization code
        String authorizationCode = authorizationCodeFuture.get();

        // Exchange authorization code for tokens
        TokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
                .setRedirectUri(clientSecrets.getDetails().getRedirectUris().get(0))
                .execute();

        return flow.createAndStoreCredential(tokenResponse, "user");
    }

    private static void openBrowser(String url) {
        Stage stage = new Stage();
        WebView webView = new WebView();
        webView.getEngine().load(url);

        // Handle redirect after authorization
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.startsWith(clientSecrets.getDetails().getRedirectUris().get(0))) {
                // Extract authorization code from the URL
                String code = extractCodeFromUrl(newValue);
                if (code != null) {
                    authorizationCodeFuture.complete(code);
                    stage.close();
                }
            }
        });

        Scene scene = new Scene(webView, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Google Sign-In");
        stage.show();
    }

    private static String extractCodeFromUrl(String url) {
        int codeIndex = url.indexOf("code=");
        if (codeIndex != -1) {
            String code = url.substring(codeIndex + 5);
            int ampIndex = code.indexOf("&");
            if (ampIndex != -1) {
                code = code.substring(0, ampIndex);
            }
            return code;
        }
        return null;
    }

    public static GoogleUserInfo getUserInfo(Credential credential) throws IOException {
        Oauth2 oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("ArtFlow")
                .build();

        Userinfo userinfo = oauth2.userinfo().get().execute();

        return new GoogleUserInfo(
                userinfo.getId(),
                userinfo.getEmail(),
                userinfo.getName(),
                userinfo.getGivenName(),
                userinfo.getFamilyName(),
                userinfo.getPicture()
        );
    }

    public static class GoogleUserInfo {
        private final String id;
        private final String email;
        private final String name;
        private final String givenName;
        private final String familyName;
        private final String pictureUrl;

        public GoogleUserInfo(String id, String email, String name, String givenName, String familyName, String pictureUrl) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.givenName = givenName;
            this.familyName = familyName;
            this.pictureUrl = pictureUrl;
        }

        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getGivenName() { return givenName; }
        public String getFamilyName() { return familyName; }
        public String getPictureUrl() { return pictureUrl; }
    }
}