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

import java.sql.SQLOutput;
import java.util.List;

@RestController
@RequestMapping("/api/produit")
@CrossOrigin(origins = "http://localhost:4200")

public class ProduitController {
    private static final Logger logger = LoggerFactory.getLogger(ReclamationController.class);

    @Autowired
    private ProduitService produitService;

    @PostMapping("/add")
    public ResponseEntity<String> create(@RequestBody Produit produit) {
        try {
            produitService.create(produit);
            return ResponseEntity.ok("Product created successfully with ID: " + produit.getId());
        } catch (Exception e) {
            logger.error("Error creating Product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Produit>> getAllProduits() {
        try {
            List<Produit> produits = produitService.getproduits();
            System.out.println(produits);
            if (produits.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(produits);
        } catch (Exception e) {
            logger.error("Error fetching Product: ", e);
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
    @GetMapping("/get/{nom}")
    public ResponseEntity<List<Produit>> getbyname(@PathVariable String nom) {
        try {
            List<Produit> produits = produitService.getProductByName(nom);
            System.out.println(produits);
            if (produits.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(produits);
        } catch (Exception e) {
            logger.error("Error fetching Product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/produitnonperime")
    public ResponseEntity<List<Produit>> getproduitperimé() {
        try {
            List<Produit> produits = produitService.getProduitpérimé();
            System.out.println(produits);
            if (produits.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(produits);
        } catch (Exception e) {
            logger.error("Error fetching Product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        produitService.delete(id);
        return ResponseEntity.ok().build();
    }
}
