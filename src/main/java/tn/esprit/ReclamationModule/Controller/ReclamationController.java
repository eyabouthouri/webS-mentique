package tn.esprit.ReclamationModule.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ReclamationModule.Service.ReclamationService;
import tn.esprit.ReclamationModule.model.Reclamation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/reclamations")
public class ReclamationController {
    private static final Logger logger = LoggerFactory.getLogger(ReclamationController.class);

    @Autowired
    private ReclamationService reclamationService;

    @PostMapping("/add")
    public ResponseEntity<String> create(@RequestBody Reclamation reclamation) {
        try {
            // Ajouter la date de soumission ici
            reclamation.setDateSoumission(new Date());

            reclamationService.create(reclamation);
            return ResponseEntity.ok("Reclamation created successfully with ID: " + reclamation.getId());
        } catch (Exception e) {
            logger.error("Error creating reclamation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reclamation: " + e.getMessage());
        }
    }

    // ...


    @GetMapping("/all")
    public ResponseEntity<List<Reclamation>> getAllReclamations() {
        try {
            List<Reclamation> reclamations = reclamationService.getAllReclamations();
            if (reclamations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reclamations);
        } catch (Exception e) {
            logger.error("Error fetching reclamations: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }}

    @GetMapping("/{id}")
    public ResponseEntity<Reclamation> read(@PathVariable String id) {
        Reclamation reclamation = reclamationService.read(id);
        if (reclamation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reclamation);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        reclamationService.delete(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search/{title}")
    public ResponseEntity<List<Reclamation>> searchByTitle(@PathVariable String title) {
        try {
            List<Reclamation> reclamations = reclamationService.searchByTitle(title);
            return ResponseEntity.ok(reclamations);
        } catch (Exception e) {
            // Gérer les erreurs ici
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/sortc")
    public ResponseEntity<List<Reclamation>> sortReclamationsByDateAsc() {
        try {
            List<Reclamation> reclamations = reclamationService.getAllReclamations();
            reclamations = reclamationService.sortReclamationsByDateAsc(reclamations);
            return ResponseEntity.ok(reclamations);
        } catch (Exception e) {
            // Gérer les erreurs ici
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

