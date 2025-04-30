package tn.artflow.services;

import tn.artflow.entities.User;
import java.sql.SQLException;
import java.util.List;


public interface IService<T> {
    void ajouter(T t) throws SQLException;
   // void modifier(int id,String nom) throws SQLException;
    void modifier(T t) throws SQLException;
    void supprimer(T t) throws SQLException;
    List<T> recuperer() throws SQLException;


    void supprimer(int id) throws SQLException;  // ✅ Supprimer par ID
    List<T> getReservationsByUser(User user) throws SQLException;

}
