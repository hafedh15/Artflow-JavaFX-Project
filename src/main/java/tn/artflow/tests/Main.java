package tn.artflow.tests;

import tn.artflow.services.UserService;
import java.util.Date;
import tn.artflow.entities.User;
import tn.artflow.tools.MyDataBase;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MyDataBase db = MyDataBase.getInstance();
        UserService usr = new UserService();

        User user1 = new User("skan","selmi","[\"ROLE_CLIENT\"]","ziziziz","zizizi@zazi.zi","bddadhxahddh",new Date(),false,true);
        User user2 = new User(68,"kiaaaa","hehe","[\"ROLE_CLIENT\"]","aaaaa","aa@aa.aa","bddadhxahddh",new Date(),false,true);
        //User userToUpdate = new User(66,"kikia","hehe","[\"ROLE_CLIENT\"]","aaaaa","aa@aa.aa","bddadhxahddh",new Date(),false,true);

         // set the ID of the user you want to update


//        User userToDelete = new User();
//        userToDelete.setId(65);
//        usr.supprimer(userToDelete);
//        System.out.println("Deletion attempt completed.");
        try{
            usr.modifier(user2);
          // usr.ajouter(user2);
          // usr.modifier(65,"skander");
             //System.out.println(usr.recuperer());
//           List<User> users = usr.recuperer();
//           for (User u : users) {
//               System.out.println(u);
//           }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
       }

//        try {
//            User loggedUser = usr.login("aa@aa.aa", "aaaaa");
//
//            if (loggedUser != null) {
//                System.out.println("Login successful! Welcome " + loggedUser.getName() + " " + loggedUser.getLastname());
//                System.out.println(loggedUser); // optional detailed print
//            } else {
//                System.out.println("Login failed: Invalid email or password.");
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Error during login: " + e.getMessage());
//        }


    }
}
