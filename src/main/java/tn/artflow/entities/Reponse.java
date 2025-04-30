package tn.artflow.entities;

import java.time.LocalDateTime;

public class Reponse {
    private int id;
    private int reclamationId;
    private int userId; // TODO: Replace with a User object when ready (ex: private User user;)
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    // Jointure: une réponse appartient à une seule réclamation
    // TODO: Replace reclamationId with an actual Reclamation object when needed
    // private Reclamation reclamation;

    public Reponse() {
    }

    public Reponse(int reclamationId, int userId, String message, boolean isRead, LocalDateTime createdAt) {
        this.reclamationId = reclamationId;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Reponse(int id, int reclamationId, int userId, String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.reclamationId = reclamationId;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReclamationId() {
        return reclamationId;
    }

    // TODO: Replace with setReclamation(Reclamation reclamation) later
    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public int getUserId() {
        return userId;
    }

    // TODO: Replace with setUser(User user) when ready
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", reclamationId=" + reclamationId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}
