package tn.esprit.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String type;
    private String password;
    private String email;
    private String photo;
    private Date dateCreation;
    private List<Product> products;
    private Cart cart;

    public User() {
        this.products = new ArrayList<>();
    }

    public User(int id, String name, String lastname, String type, String password, String email, String photo, Date dateCreation) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.type = type;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.dateCreation = dateCreation;
        this.products = new ArrayList<>();
    }

    public User(int userId, String userName) {
        this.id = userId;
        this.name = userName;
        this.products = new ArrayList<>();
    }

    // Getters et setters pour les attributs existants...

    // Méthodes pour gérer la liste de produits
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
        product.setUser(this);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }

    // Méthodes pour gérer le panier
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
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
                ", dateCreation=" + dateCreation +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}