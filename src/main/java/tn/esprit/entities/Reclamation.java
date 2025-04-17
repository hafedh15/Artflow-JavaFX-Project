package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reclamation {
    private int id;
    private int userId; // TODO: Replace with a User object when ready (ex: private User user;)
    private String subject;
    private String message;
    private String status;
    private boolean isMarked;
    private LocalDateTime createdAt;

    // Jointure: Une réclamation peut avoir plusieurs réponses
    private List<Reponse> reponses = new ArrayList<>();

    public Reclamation() {
    }

    public Reclamation(int userId, String subject, String message, String status, boolean isMarked, LocalDateTime createdAt) {
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.isMarked = isMarked;
        this.createdAt = createdAt;
    }

    public Reclamation(int id, int userId, String subject, String message, String status, boolean isMarked, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.isMarked = isMarked;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    // TODO: Replace with setUser(User user) when ready
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Reponse> getReponses() {
        return reponses;
    }

    public void setReponses(List<Reponse> reponses) {
        this.reponses = reponses;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", userId=" + userId +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", isMarked=" + isMarked +
                ", createdAt=" + createdAt +
                ", reponses=" + reponses +
                '}';
    }
}
