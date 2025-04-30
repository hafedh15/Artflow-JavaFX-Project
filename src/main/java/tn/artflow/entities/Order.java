package tn.artflow.entities;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private Cart cart;
    private User user;
    private String deliveryAddress;
    private String phoneNumber;
    private Date dateOrder;
    private String orderHistory;
    private Boolean paid;
    private Integer paymentIntentId;
    private List<Product> products;

    // Constructeurs
    public Order() {
        this.products = new ArrayList<>();
    }

    public Order(Integer id, Cart cart, User user, String deliveryAddress, String phoneNumber,
                 Date dateOrder, String orderHistory, Boolean paid, Integer paymentIntentId) {
        this.id = id;
        this.cart = cart;
        this.user = user;
        this.deliveryAddress = deliveryAddress;
        this.phoneNumber = phoneNumber;
        this.dateOrder = dateOrder;
        this.orderHistory = orderHistory;
        this.paid = paid;
        this.paymentIntentId = paymentIntentId;
        this.products = new ArrayList<>();
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
        // Si le panier a des produits, les ajouter à la commande
        if (cart != null && cart.getProducts() != null) {
            this.products.addAll(cart.getProducts());
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(String orderHistory) {
        this.orderHistory = orderHistory;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Integer getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(Integer paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }

    // Méthode pour calculer le total de la commande
    public Double calculateTotal() {
        if (this.cart != null && this.cart.getTotalPrice() != null) {
            return this.cart.getTotalPrice();
        } else if (this.products != null && !this.products.isEmpty()) {
            return this.products.stream()
                    .mapToDouble(Product::getPrice)
                    .sum();
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", cart_id=" + (cart != null ? cart.getId() : "null") +
                ", user_id=" + (user != null ? user.getId() : "null") +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOrder=" + dateOrder +
                ", orderHistory='" + orderHistory + '\'' +
                ", paid=" + paid +
                ", paymentIntentId=" + paymentIntentId +
                ", total=" + calculateTotal() +
                '}';
    }
}