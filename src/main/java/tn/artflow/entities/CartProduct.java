package tn.artflow.entities;

public class CartProduct {
    private Integer id;
    private Cart cart;
    private Product product;

    public CartProduct() {
    }

    public CartProduct(Integer id, Cart cart, Product product) {
        this.id = id;
        this.cart = cart;
        this.product = product;
    }

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
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "CartProduct{" +
                "id=" + id +
                ", cart=" + (cart != null ? cart.getId() : "null") +
                ", product=" + (product != null ? product.getName() : "null") +
                '}';
    }
}