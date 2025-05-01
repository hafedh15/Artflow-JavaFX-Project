package tn.artflow.tools;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class CallbackServer {
    private HttpServer server;
    private CompletableFuture<String> authCodeFuture = new CompletableFuture<>();
    private int port;

    public CallbackServer() throws IOException {
        // Try several ports, starting from 8000
        IOException lastException = null;
        for (int tryPort = 8000; tryPort < 9000; tryPort++) {
            try {
                server = HttpServer.create(new InetSocketAddress(tryPort), 0);
                this.port = tryPort;
                // If we get here, the port worked
                break;
            } catch (IOException e) {
                lastException = e;
                // Continue with the next port
            }
        }

        // If no port worked
        if (server == null) {
            throw new IOException("Unable to find an available port", lastException);
        }

        server.createContext("/callback", exchange -> {
            System.out.println("Received callback request: " + exchange.getRequestURI());

            String query = exchange.getRequestURI().getQuery();
            String code = null;
            String error = null;

            if (query != null) {
                // Parse query parameters properly
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

                        if ("code".equals(key)) {
                            code = value;
                            System.out.println("Authorization code received: " + code);
                        } else if ("error".equals(key)) {
                            error = value;
                            System.out.println("Error received: " + error);
                        }
                    }
                }
            }

            String response;
            if (code != null) {
                response = "<html><body style='font-family:Arial,sans-serif;'>" +
                        "<div style='text-align:center;margin-top:50px;'>" +
                        "<h2>Authentication successful!</h2>" +
                        "<p>You can now close this window.</p>" +
                        "</div></body></html>";

                authCodeFuture.complete(code);
            } else {
                String errorMessage = error != null ? error : "Authentication code not found";
                response = "<html><body style='font-family:Arial,sans-serif;'>" +
                        "<div style='text-align:center;margin-top:50px;'>" +
                        "<h2>Authentication failed</h2>" +
                        "<p>Error: " + errorMessage + "</p>" +
                        "<p>Please close this window and try again.</p>" +
                        "</div></body></html>";

                authCodeFuture.completeExceptionally(
                        new RuntimeException("Authentication failed: " + errorMessage));
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }

            // Stop the server after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    server.stop(0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    public void start() {
        server.start();
        System.out.println("Callback server started on port " + port);
        System.out.println("Redirect URI: http://localhost:" + port + "/callback");
    }

    public CompletableFuture<String> getAuthCodeFuture() {
        return authCodeFuture;
    }

    public int getPort() {
        return port;
    }
}