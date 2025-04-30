package tn.artflow.entities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import javafx.scene.control.TextInputDialog;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GoogleSignIn {

    private static final String CLIENT_ID = "978729700643-b8lv7iodhbi7fohtg8j1jtk5qnb20t8c.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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"; // replace with actual secret
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of("profile", "email");

    public static Credential authorize() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // Manually create GoogleClientSecrets
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                .setInstalled(new GoogleClientSecrets.Details()
                        .setClientId(CLIENT_ID)
                        .setClientSecret(CLIENT_SECRET)
                        .setRedirectUris(List.of("urn:ietf:wg:oauth:2.0:oob"))
                );

        var flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        // Show authorization URL
        String redirectUri = "urn:ietf:wg:oauth:2.0:oob";
        GoogleAuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

        // Make sure this class runs on the JavaFX thread (more on that below)

        String url = authorizationUrl.build();
        System.out.println("Opening browser to: " + url);

        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } else {
            System.out.println("Please open the following URL manually in your browser:");
            System.out.println(url);
        }

// Show JavaFX input dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Google Authentication");
        dialog.setHeaderText("Enter the code from your browser");
        dialog.setContentText("Code:");

        String code = dialog.showAndWait().orElseThrow(() ->
                new RuntimeException("No code entered, Google Sign-In cancelled."));

        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
        return flow.createAndStoreCredential(tokenResponse, "user");

    }
}
