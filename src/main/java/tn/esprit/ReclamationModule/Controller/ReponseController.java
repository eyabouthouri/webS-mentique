package tn.esprit.ReclamationModule.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ReclamationModule.Service.ReclamationService;
import tn.esprit.ReclamationModule.Service.ReponseService;
import tn.esprit.ReclamationModule.model.Reclamation;
import tn.esprit.ReclamationModule.model.Reponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/reponses")
public class ReponseController {

    private static final Logger logger = LoggerFactory.getLogger(ReponseController.class);

    @Autowired
    private ReponseService reponseService;

    @Autowired
    private ReclamationService reclamationService;

    @PostMapping("/ad/{reclamationId}")
    public ResponseEntity<?> addReponseToReclamation(@PathVariable String reclamationId, @RequestBody Reponse reponse) {
        System.out.println("Received reclamationId from URL: " + reclamationId);  // Logging
        // Assurez-vous que l'objet reponse a le bon ID de réclamation
        Reclamation reclamation = new Reclamation();
        reclamation.setId(reclamationId);
        reponse.setReclamation(reclamation);

        reponseService.create(reponse);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<String> create(@PathVariable String id, @RequestBody Reponse reponse) {
        try {
            Reclamation reclamation = reclamationService.read(id);
            if (reclamation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reclamation not found with ID: " + id);
            }
            reponse.setReclamation(reclamation);

            reponseService.create(reponse);
            return ResponseEntity.ok("Response created successfully with ID: " + reponse.getId());
        } catch (Exception e) {
            logger.error("Error creating response: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating response: " + e.getMessage());
        }
    }



    @GetMapping("/byReclamation/{id}")
    public ResponseEntity<Reponse> getReponseByReclamationId(@PathVariable("id") String id) {
        try {
            Reponse reponse = reponseService.getReponseByReclamationId(id);
            if (reponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(reponse);
        } catch (Exception e) {
            logger.error("Error fetching response for reclamation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Reponse>> getAllReponses() {
        try {
            List<Reponse> reponses = reponseService.getAllReponses();
            if (reponses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // No data found
            }
            return ResponseEntity.ok(reponses);
        } catch (Exception e) {
            logger.error("Error fetching all responses: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    //... (other methods such as update, delete, and find can be added as needed)
}
