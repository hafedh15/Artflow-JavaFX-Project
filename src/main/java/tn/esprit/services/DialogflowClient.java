package tn.esprit.services;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class DialogflowClient {

    private static final String PROJECT_ID = "reclamationbot-9n9u";
    private static final String JSON_KEY_PATH = "C:\\Users\\HP\\Desktop\\pidev3A52\\reclamationbot-9n9u-6f40a425708a.json";

    public static String detectIntent(String text) throws IOException {
        // Load credentials from JSON
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(JSON_KEY_PATH));
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // Create a session
        try (SessionsClient sessionsClient = SessionsClient.create(sessionsSettings)) {
            SessionName session = SessionName.of(PROJECT_ID, UUID.randomUUID().toString());

            TextInput textInput = TextInput.newBuilder()
                    .setText(text)
                    .setLanguageCode("fr") // Adjust if using another language
                    .build();

            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(request);
            return response.getQueryResult().getFulfillmentText();
        }
    }
}
