package tn.artflow.tests;

import tn.artflow.entities.*;
import tn.artflow.services.*;

import java.util.Date;
import java.util.List;

import tn.artflow.tools.MyDataBase;

import javax.management.relation.RelationService;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        WorkshopService ws = new WorkshopService();
      //  Workshop w = new Workshop( "loody", "hades", "image.jpg", new Date(), "Online", "Zoom", 0.0f, 0.0f);
        ReservationService rs = new ReservationService();
        try {
/* Ajouter un workshop
            ws.ajouter(w);


           ws.modifier(17, "mwah");
           ws.supprimer(17);

*/
           // ws.modifier(77, "mwah");
            //System.out.println(ws.recuperer());

            UserService userService = new UserService();
            User user = new User();
            user.setName("Nasr");
            user.setLastname("Test");
            user.setEmail("nasr@test.com");
            user.setPassword("123456");
            user.setPhoto("photo.jpg");
            user.setType("USER");
            user.setDateCreation(new Date());

// ajoute l'utilisateur
            userService.ajouter(user);

            int id = user.getId(); // ✅ OK ici car 'user' est un objet
            System.out.println("ID inséré : " + id);
// récupère l’utilisateur avec l’ID inséré (à adapter selon ton autoincrement)

           Workshop work = new Workshop();
            work.setId(18); // assure-toi que l'article avec id = 1 existe

            //Reservation r = new Reservation(user, work, 2, "hades" ,new Date(), "loody123");
         // rs.ajouter(r);

             //rs.modifier(84, "note mis à jour");
         //   rs.supprimer(84);



            List<Reservation> reservations = rs.recuperer();
            for (Reservation res : reservations) {
                System.out.println(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }




}
