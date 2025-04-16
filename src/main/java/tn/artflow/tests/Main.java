package tn.artflow.tests;

import tn.artflow.services.ArticleService;
import tn.artflow.services.UserService;

import java.util.Date;
import java.util.List;
import tn.artflow.entities.Comment;
import tn.artflow.entities.User;
import tn.artflow.services.CommentService;
import tn.artflow.entities.Article;
import tn.artflow.tools.MyDataBase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        ArticleService articleService = new ArticleService();

        try {
            Article article = new Article("Titre 1", "Contenu de l'article", "2025-04-12",
                    "image.jpg", "Categorie 1", "Auteur 1", 0);
            articleService.ajouter(article);
            System.out.println("Article ajouté avec succès !");



            // Modifier un article
// Récupère l'article à modifier
           /* Article articleModif = articleService.recuperer(6); // assure-toi que l'article 6 existe

            if (articleModif != null) {
                articleModif.setTitre("Titre modifié");
                articleModif.setContenu("Contenu mis à jour");
                articleModif.setCategorie("Nouvelle Catégorie");
                articleModif.setNomAuteur("Auteur modifié");
                articleModif.setImage("nouvelle-image.jpg");
                articleModif.setDatepub("2025-04-30");
                articleModif.setViews(articleModif.getViews() + 1);

                articleService.modifier(articleModif); // ✅ Appel correct
                System.out.println("✅ Article modifié avec succès !");
            } else {
                System.out.println("❌ Article ID 6 introuvable");
            }

*/
            // Afficher les articles
            List<Article> articles = articleService.recuperer();
            for (Article a : articles) {
                System.out.println(a);
            }

            // Supprimer un article
            articleService.supprimer(5);  // ✅ Passe maintenant un int
            System.out.println("Article supprimé avec succès !");
            CommentService cs = new CommentService();


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

            Article articleComment = new Article();
           articleComment.setId(8); // assure-toi que l'article avec id = 1 existe

            Comment c = new Comment(42, "Super article !", "2025-04-12", user, articleComment, 5);
            cs.ajouter(c);

           // cs.modifier(40, "Commentaire mis à jour");

            List<Comment> commentaires = cs.recuperer();
            for (Comment com : commentaires) {
                System.out.println(com);
            }

            cs.supprimer(31);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }




}
