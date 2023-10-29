package tn.esprit.ReclamationModule.Controller;

import tn.esprit.ReclamationModule.Service.MedecinService;
import tn.esprit.ReclamationModule.Service.RdvService;
import tn.esprit.ReclamationModule.model.Medecin;
import tn.esprit.ReclamationModule.model.RendezVous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


@RestController
@RequestMapping("/api/rdv")
public class RendezVousController {
    private static final Logger logger = LoggerFactory.getLogger(MedecinController.class);

    @Autowired
    RdvService rdvService;

    @Autowired
    MedecinService medecinService;

    @PostMapping("/ad/{medecinId}")
    public ResponseEntity<?> addReponseToReclamation(@PathVariable String medecinId, @RequestBody RendezVous rdv) {
        System.out.println("Received reclamationId from URL: " + medecinId);  // Logging
        // Assurez-vous que l'objet reponse a le bon ID de réclamation
        Medecin medecin = new Medecin();
        medecin.setMedecinId(medecinId);
        rdv.setMedecin(medecin);

        rdvService.create(rdv);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/all/{id}")
    public ResponseEntity<List<RendezVous>> getAllRdv(@PathVariable("id") String id) {
        try {
            List<RendezVous> rdvs = rdvService.getAllRdvs(id);
            if (rdvs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(rdvs);
        } catch (Exception e) {
            logger.error("Error fetching reclamations: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }}

}
