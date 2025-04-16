package tn.artflow.entities;

import java.util.Date;

public class Comment {
    private int id;
    private String contenuComment;
    private Date datecom;
    private int rating; // Ajout de l'attribut rating

    // Constructeur par défaut
    public Comment() {
    }

    // Constructeur avec paramètres
    public Comment(int id, String contenuComment, Date datecom, int rating) {
        this.id = id;
        this.contenuComment = contenuComment;
        this.datecom = datecom;
        this.rating = rating;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenuComment() {
        return contenuComment;
    }

    public void setContenuComment(String contenuComment) {
        this.contenuComment = contenuComment;
    }

    public Date getDatecom() {
        return datecom;
    }

    public void setDatecom(Date datecom) {
        this.datecom = datecom;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", contenuComment='" + contenuComment + '\'' +
                ", datecom=" + datecom +
                ", rating=" + rating +
                '}';
    }
}
