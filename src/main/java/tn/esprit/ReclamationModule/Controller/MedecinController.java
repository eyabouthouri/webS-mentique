package tn.esprit.ReclamationModule.Controller;

import tn.esprit.ReclamationModule.Service.MedecinService;
import tn.esprit.ReclamationModule.model.Medecin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/medecins")
public class MedecinController {
    private static final Logger logger = LoggerFactory.getLogger(MedecinController.class);

    @Autowired
    private MedecinService medecinService;

    @PostMapping("/add")
    public ResponseEntity<String> create(@RequestBody Medecin reclamation) {
        try {
            medecinService.create(reclamation);
            return ResponseEntity.ok("Reclamation created successfully with ID: " + reclamation.getMedecinId());
        } catch (Exception e) {
            logger.error("Error creating reclamation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reclamation: " + e.getMessage());
        }
    }

    // ...


    @GetMapping("/all")
    public ResponseEntity<List<Medecin>> getAllReclamations() {
        try {
            List<Medecin> reclamations = medecinService.getAllMedecins();
            if (reclamations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reclamations);
        } catch (Exception e) {
            logger.error("Error fetching reclamations: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }}

    @GetMapping("/{id}")
    public ResponseEntity<Medecin> read(@PathVariable String id) {
        Medecin reclamation = medecinService.read(id);
        if (reclamation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reclamation);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        medecinService.delete(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search/{prenom}")
    public ResponseEntity<List<Medecin>> searchByTitle(@PathVariable String prenom) {
        try {
            List<Medecin> medecins = medecinService.searchByNom(prenom);

            return ResponseEntity.ok(medecins);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
