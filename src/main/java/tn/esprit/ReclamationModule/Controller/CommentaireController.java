package tn.esprit.ReclamationModule.Controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ReclamationModule.Service.ArticleService;
import tn.esprit.ReclamationModule.Service.CommentaireService;
import tn.esprit.ReclamationModule.model.Article;
import tn.esprit.ReclamationModule.model.Commentaire;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/commentaires")
public class CommentaireController {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReclamationController.class);



    @Autowired
    private CommentaireService commentaireService;

    @Autowired
    private ArticleService articleService;

    @PostMapping("/add/{id}")
    public ResponseEntity<String> create(@PathVariable String id, @RequestBody Commentaire commentaire) {
        try {
            Article article = articleService.read(id);
            if (article == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Comment not found with ID: " + id);
            }
            commentaire.setArticle(article);

            commentaireService.create(commentaire);
            return ResponseEntity.ok("Comment created successfully with ID: " + commentaire.getId());
        } catch (Exception e) {
            logger.error("Error creating commentaire: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating response: " + e.getMessage());
        }
    }



   /* @GetMapping("/byReclamation/{id}")
    public ResponseEntity<Commentaire> getReponseByReclamationId(@PathVariable("id") String id) {
        try {
            Commentaire commentaire = commentaireService.getReponseByReclamationId(id);
            if (commentaire == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(commentaire);
        } catch (Exception e) {
            logger.error("Error fetching response for reclamation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }*/
    @GetMapping("/all")
    public ResponseEntity<List<Commentaire>> getAllReponses() {
        try {
            List<Commentaire> commentaires = commentaireService.getAllComments();
            if (commentaires.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // No data found
            }
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            logger.error("Error fetching all responses: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




}
