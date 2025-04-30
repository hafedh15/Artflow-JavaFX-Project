package tn.artflow.entities;

public class Product {
    private int id;
    private User user;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String category;
    private String image;
    private String status;

    public Product() {
    }

    public Product(int id, User user, String name, String description, double price, int stock, String category, String image, String status) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.image = image;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", user={" +
                "id=" + user.getId() +
                ", name='" + user.getName()  + '\'' +
                ", lastname='" + user.getLastname() + '\'' +

                ", password='" + user.getPassword() + '\'' +
              //  ", email='" + (user != null ? user.getEmail() : "null") + '\'' +
             //   ", photo='" + (user != null ? user.getPhoto() : "null") + '\'' +
             //   ", dateCreation=" + (user != null ? user.getDateCreation() : "null") +
                "}, " +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                ", status='" + status + '\'' +
                '}';
    }


}
