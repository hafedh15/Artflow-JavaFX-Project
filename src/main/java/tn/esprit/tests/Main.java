package tn.esprit.tests;

import tn.esprit.entities.Reclamation;
import tn.esprit.services.ReclamationService;

import tn.esprit.entities.Reponse;
import tn.esprit.services.ReponseService;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        ReclamationService rs = new ReclamationService();

        // Create a new Reclamation (example user_id = 1)
        Reclamation r = new Reclamation(1, "Problème de connexion", "Je n'arrive pas à me connecter", "Open", false, LocalDateTime.now());

        try {
            // Add the Reclamation
            //rs.ajouter(r);

            // Modify subject of a Reclamation
            rs.modifier(12, "Sujet modifié");

            // Delete a Reclamation
            rs.supprimer(new Reclamation(13, 1, "", "", "", false, LocalDateTime.now()));

            // Display all
            System.out.println(rs.recuperer());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//HETHI LEL REPONSE TASWA

        ReponseService repService = new ReponseService();

        // Example: user_id = 2, reclamation_id = 1
        Reponse rep = new Reponse(12, 2, "Nous avons reçu votre réclamation", false, LocalDateTime.now());

        try {
            // Add the Reponse
           // repService.ajouter(rep);

            // Modify Reponse message
            repService.modifier(168, "Message modifié par l'admin");

            // Delete Reponse
            repService.supprimer(new Reponse(169, 1, 2, "", false, LocalDateTime.now()));

            // Display all
            System.out.println(repService.recuperer());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
