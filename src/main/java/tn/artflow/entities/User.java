package tn.artflow.entities;


import java.util.Date;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String type;
    private String password;
    private String email;
    private String photo;
    private Date date_creation;

    public User() {
        this.date_creation = new Date(); // Initialisation automatique

    }
    public User(int id) {
        this.id = id;
    }

    public User(String name, String lastname, String type, String password, String email, String photo, Date date_creation) {

        this.name = name;
        this.lastname = lastname;
        this.type = type;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.date_creation = date_creation;
    }

    public User(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getDateCreation() {
        return date_creation;
    }

    public void setDateCreation(Date dateCreation) {
        this.date_creation = date_creation;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", type='" + type + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", dateCreation=" + date_creation +
                '}';
    }
}
