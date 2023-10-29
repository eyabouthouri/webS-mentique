package tn.esprit.ReclamationModule.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ReclamationModule.Service.ArticleService;
import tn.esprit.ReclamationModule.model.Article;
import tn.esprit.ReclamationModule.model.Reclamation;


import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/articles")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ReclamationController.class);

    @Autowired
    private ArticleService articleService;

    @PostMapping("/add")
    public ResponseEntity<String> create(@RequestBody Article article) {
        try {
            articleService.create(article);
            return ResponseEntity.ok("Reclamation created successfully with ID: " + article.getId());
        } catch (Exception e) {
            logger.error("Error creating reclamation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reclamation: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Article>> getAllReclamations() {
        try {
            List<Article> articles = articleService.getarticles();
            if (articles.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching reclamations: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }}

    @GetMapping("/{id}")
    public ResponseEntity<Article> read(@PathVariable String id) {
        Article article = articleService.read(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        articleService.delete(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search/{title}")
    public ResponseEntity<List<Article>> searchByTitle(@PathVariable String title) {
        try {
            List<Article> articles = articleService.searchByTitle(title);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            // Gérer les erreurs ici
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
