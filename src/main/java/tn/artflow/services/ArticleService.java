package tn.artflow.services;


import tn.artflow.entities.Article;
import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleService implements IService<Article>{
    Connection cnx;
    String sql;
    public ArticleService(){
        cnx= MyDataBase.getInstance().getCnx();
    }
    @Override
    public void ajouter(Article article) throws SQLException {
        sql="insert into article(titre,contenu,datepub,image,categorie,nom_auteur,views)" +
                "values(?,?,?,?,?,?,?)";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setString(1, article.getTitre()); // ✅ Correction : setString pour contenu
        ste.setString(2, article.getContenu()); // ✅ Correction : setString pour contenu
        ste.setString(3, article.getDatepub()); // ✅ Correction : setString pour contenu

        ste.setString(4, article.getImage());
        ste.setString(5,article.getCategorie());
        ste.setString(6,article.getNomAuteur());
        ste.setInt(7,article.getViews());

        int rows = ste.executeUpdate();
        System.out.println("🚀 Nombre de lignes insérées : " + rows);

        System.out.println("article  ajoutée ");
    }
    @Override
    public void modifier(Article article) throws SQLException {
        sql = "UPDATE article SET titre = ?, contenu = ?, datepub = ?, image = ?, categorie = ?, nom_auteur = ?, views = ? WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setString(1, article.getTitre());
        ste.setString(2, article.getContenu());
        ste.setDate(3, java.sql.Date.valueOf(article.getDatepub()));
        ste.setString(4, article.getImage());
        ste.setString(5, article.getCategorie());
        ste.setString(6, article.getNomAuteur());
        ste.setInt(7, article.getViews()); // si tu ne modifies pas les vues, tu peux laisser le même
        ste.setInt(8, article.getId()); // ⚠️ Tu DOIS avoir l'ID

        ste.executeUpdate();
    }



    @Override
    public void supprimer(int id) throws SQLException {
        sql = "DELETE FROM article WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setInt(1, id);
        ste.executeUpdate();
        System.out.println("Article supprimé !");
    }


    @Override
    public List<Article> recuperer() throws SQLException {
        List<Article> articles = new ArrayList<>();
        sql = "SELECT * FROM article";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);

        while (rs.next()) {
            Article article = new Article(
                  //  rs.getInt("id"),

                    rs.getString("titre"),
                    rs.getString("contenu"),
                    rs.getString("datepub"),
                    rs.getString("image"),
                    rs.getString("categorie"),
                    rs.getString("nom_auteur"),
                    rs.getInt("views")

            );
            article.setId(rs.getInt("id")); // 🔥 C'EST ESSENTIEL
            articles.add(article);        }
        return articles;
    }



}
