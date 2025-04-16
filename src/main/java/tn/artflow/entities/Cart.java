package tn.artflow.entities;


import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Integer id;
    private Double totalPrice;
    private User user;
    private List<Product> products;

    public Cart() {
        this.products = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    public Cart(Integer id, User user) {
        this.id = id;
        this.user = user;
        this.products = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
        this.totalPrice += product.getPrice();
    }

    public void removeProduct(Product product) {
        if (this.products.remove(product)) {
            this.totalPrice -= product.getPrice();
        }
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", totalPrice=" + totalPrice +
                ", user=" + user +
                ", products=" + products +
                '}';
    }
}