package tn.artflow.entities;

import java.util.Date;

public class Comment {
    private int id;
    private String contenu_Comment;
    private String datecom;
    private User user;
    private Article article;
    private int rating; // Ajout de l'attribut rating

    // Constructeur par défaut
    public Comment() {
    }

    // Constructeur avec paramètres
    public Comment(int id, String contenu_Comment, String datecom, User user, Article article, int rating) {
        this.id = id;
        this.contenu_Comment = contenu_Comment;
        this.datecom = datecom;
        this.user = user;
        this.article = article;
        this.rating = rating;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu_Comment() {
        return contenu_Comment;
    }

    public void setContenu_Comment(String contenu_Comment) {
        this.contenu_Comment = contenu_Comment;
    }

    public String getDatecom() {
        return datecom;
    }

    public void setDatecom(String datecom) {
        this.datecom = datecom;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

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
                ", contenuComment='" + contenu_Comment + '\'' +
                ", datecom=" + datecom +
                ", user=" + user.getName() + " " + user.getLastname() +
               // ", articleId=" + article.getId()= +
                ", rating=" + rating +
                '}';
    }
}
