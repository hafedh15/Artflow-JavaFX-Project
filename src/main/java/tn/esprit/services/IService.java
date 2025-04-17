package tn.esprit.services;

import tn.esprit.entities.Product;
import tn.esprit.entities.User;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    List<Product> getProductsByUser(User user) throws SQLException;

    void ajouter(T t) throws SQLException;
    void modifier(int id,String nom) throws SQLException;

    //void supprimerParId(int cartId) throws SQLException;

    void modifier(Product product) throws SQLException;

    void modifierP(Product product) throws SQLException;

    void supprimer(T t) throws SQLException;

    List<T> recuperer() throws SQLException;
}
