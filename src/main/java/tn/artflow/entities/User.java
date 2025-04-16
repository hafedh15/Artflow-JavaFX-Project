package tn.artflow.entities;


import java.util.Date;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String roles;
    private String password;
    private String email;
    private String photo;
    private Date dateCreation;
    private boolean isBanned;
    private boolean isVerified;


    public User() {
    }


    public User(int id,String name, String lastname, String roles, String password, String email, String photo, Date dateCreation, boolean isBanned, boolean isVerified) {

        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.roles = roles;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.dateCreation = dateCreation;
        this.isBanned = isBanned;
        this.isVerified = isVerified ;
    }

    public User(String name, String lastname, String roles, String password, String email, String photo, Date dateCreation, boolean isBanned, boolean isVerified) {

        this.name = name;
        this.lastname = lastname;
        this.roles = roles;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.dateCreation = dateCreation;
        this.isBanned = isBanned;
        this.isVerified = isVerified ;
    }


    public User(String name, String lastname,   String email, String photo, Date dateCreation, boolean isBanned, boolean isVerified) {

        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.photo = photo;
        this.dateCreation = dateCreation;
        this.isBanned = isBanned;
        this.isVerified = isVerified ;
    }
    public User(String name, String lastname,   String email, String Password) {

        this.name = name;
        this.lastname = lastname;
        this.email = email;
       this.password = Password;
    }

    public User(int Id, String name) {
        this.name = name;
        this.id = id;
    }

    public User(int id,String name, String lastname, String email, Date dateCreation, Boolean verification) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.dateCreation = dateCreation;
        this.isVerified = verification;
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
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
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean getIsBanned() {
        return isBanned;
    }
    public void setIs_Banned(boolean isBanned) {
        this.isBanned = isBanned;
    }
    public boolean getIsVerified() {
        return isVerified;
    }
    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", type='" + roles + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", dateCreation=" + dateCreation +
                ", status='" + (isBanned ? "Banned" : "Not Banned") + '\'' +
                ", verification='" + (isVerified ? "Verified" : "Not Verified") + '\'' +
                '}';
    }
}
