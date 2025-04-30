package tn.artflow.services;

import tn.artflow.entities.Article;
import tn.artflow.entities.Comment;
import tn.artflow.entities.User;

import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService implements IService<Comment>
{
    private Connection cnx;
    private String sql;

    public CommentService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    public void ajouter(Comment comment) throws SQLException {
        sql = "INSERT INTO comment(contenu_comment, datecom, user_id, article_id, rating) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setString(1, comment.getContenu_Comment());
        ste.setString(2, comment.getDatecom());
        ste.setInt(3, comment.getUser().getId());
        ste.setInt(4, comment.getArticle().getId()); // ✅ Ajout nécessaire
        ste.setInt(5, comment.getRating());

        try {
            ste.executeUpdate();
            System.out.println("✅ Commentaire ajouté !");
        } catch (SQLException e) {
            System.out.println("❌ Échec de l'ajout du commentaire : " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void modifier(Comment comment) throws SQLException {
        sql = "UPDATE comment SET contenu_comment = ?, datecom = ?, rating = ? WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setString(1, comment.getContenu_Comment());
        ste.setString(2, comment.getDatecom()); // si stockée au format String
        ste.setInt(3, comment.getRating());
        ste.setInt(4, comment.getId());

        ste.executeUpdate();
        System.out.println("✅ Commentaire modifié !");
    }

    @Override
    public void supprimer(Comment comment) {

    }


    @Override
    public void supprimer(int id) throws SQLException {
        sql = "DELETE FROM comment WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setInt(1, id);
        ste.executeUpdate();
        System.out.println("Commentaire supprimé !");
    }

    @Override
    public List<Comment> getReservationsByUser(User user) throws SQLException {
        return List.of();
    }

    @Override
    public List<Comment> recuperer() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        sql = "SELECT c.*, u.name, u.lastname, a.titre FROM comment c " +
                "JOIN user u ON c.user_id = u.id " +
                "JOIN article a ON c.article_id = a.id";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);

        while (rs.next()) {
            Comment comment = new Comment();
            comment.setId(rs.getInt("id"));
            comment.setContenu_Comment(rs.getString("contenu_Comment"));
            comment.setDatecom(rs.getString("datecom"));
            comment.setRating(rs.getInt("rating"));

            // Création des objets associés
            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setName(rs.getString("name"));
            user.setLastname(rs.getString("lastname"));
            comment.setUser(user);

            Article article = new Article();
            // article.setId(rs.getInt("article_id"));
            article.setTitre(rs.getString("titre"));
            comment.setArticle(article);

            comments.add(comment);
        }
        return comments;
    }


    public List<Comment> recupererParArticle(int articleId) {
        List<Comment> comments = new ArrayList<>();

        try {
            String sql = "SELECT c.*, u.name, u.photo FROM comment c JOIN user u ON c.user_id = u.id WHERE c.article_id = ?";
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, articleId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
               // User user = new User();
                User user = tn.artflow.utils.UserSession.getInstance().getUser();
                System.out.println(user);

                user.setName(rs.getString("name"));
                user.setPhoto(user.getPhoto()); // <-- photo récupérée

                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setContenu_Comment(rs.getString("contenu_comment"));
                comment.setDatecom(rs.getString("datecom"));
                comment.setRating(rs.getInt("rating"));
                comment.setUser(user);

                comments.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return comments;
    }

}
