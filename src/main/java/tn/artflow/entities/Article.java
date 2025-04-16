package tn.artflow.entities;
import java.util.Date;

public class Article {
    private int id;
    private String titre;
    private String contenu;
    private Date datepub;
    private String image;
    private String categorie;
    private String nomAuteur;
    private int views;

    // Constructeur par défaut
    public Article() {
    }

    // Constructeur avec paramètres
    public Article(int id, String titre, String contenu, Date datepub, String image, String categorie, String nomAuteur, int views) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.datepub = datepub;
        this.image = image;
        this.categorie = categorie;
        this.nomAuteur = nomAuteur;
        this.views = views;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDatepub() {
        return datepub;
    }

    public void setDatepub(Date datepub) {
        this.datepub = datepub;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getNomAuteur() {
        return nomAuteur;
    }

    public void setNomAuteur(String nomAuteur) {
        this.nomAuteur = nomAuteur;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", datepub=" + datepub +
                ", image='" + image + '\'' +
                ", categorie='" + categorie + '\'' +
                ", nomAuteur='" + nomAuteur + '\'' +
                ", views=" + views +
                '}';
    }
}
