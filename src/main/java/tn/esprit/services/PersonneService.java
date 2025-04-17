package tn.esprit.services;

import tn.esprit.entities.Personne;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements IService<Personne>{
    Connection cnx;
    String sql;
    public PersonneService(){
        cnx= MyDataBase.getInstance().getCnx();
    }
    @Override
    public void ajouter(Personne personne) throws SQLException {
        sql="insert into personne(nom,prenom,age)" +
                "values(?,?,?)";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setString(1,personne.getNom());
        ste.setInt(3,personne.getAge());
        ste.setString(2,personne.getPrenom());
        ste.executeUpdate();
        System.out.println("Personne ajoutée ");
    }

    @Override
    public void modifier(int id, String nom) throws SQLException {
        sql="update personne set nom='"+nom+"' where id="+id;
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
        System.out.println("personne modifiée");
    }

    @Override
    public void supprimer(Personne personne) {

    }

    @Override
    public List<Personne> recuperer() throws SQLException {
        sql="select * from personne";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Personne> personnes = new ArrayList<>();
        while(rs.next()){
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            int age = rs.getInt("age");
            int id = rs.getInt("id");
            Personne p = new Personne(id,age,nom,prenom);
            personnes.add(p);
        }
        return personnes;
    }
}
