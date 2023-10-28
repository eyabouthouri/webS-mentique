package tn.esprit.ReclamationModule.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ReclamationModule.Service.ProduitService;
import tn.esprit.ReclamationModule.Service.ReclamationService;
import tn.esprit.ReclamationModule.model.Produit;
import tn.esprit.ReclamationModule.model.Reclamation;

import java.util.List;

@RestController
@RequestMapping("/api/produit")
public class ProduitController {
    private static final Logger logger = LoggerFactory.getLogger(ReclamationController.class);

    @Autowired
    private ProduitService produitService;

    @PostMapping("/add")
    public ResponseEntity<String> create(@RequestBody Produit produit) {
        try {
            produitService.create(produit);
            return ResponseEntity.ok("Reclamation created successfully with ID: " + produit.getId());
        } catch (Exception e) {
            logger.error("Error creating reclamation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reclamation: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Produit>> getAllReclamations() {
        try {
            List<Produit> produits = produitService.getproduits();
            if (produits.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(produits);
        } catch (Exception e) {
            logger.error("Error fetching reclamations: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }}

    @GetMapping("/{id}")
    public ResponseEntity<Produit> read(@PathVariable String id) {
        Produit produit = produitService.read(id);
        if (produit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produit);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        produitService.delete(id);
        return ResponseEntity.ok().build();
    }
}
