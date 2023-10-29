package tn.esprit.ReclamationModule.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ReclamationModule.Service.CommandeService;
import tn.esprit.ReclamationModule.Service.ProduitService;
import tn.esprit.ReclamationModule.model.Commande;
import tn.esprit.ReclamationModule.model.Produit;

import java.util.List;

@RestController
@RequestMapping("/commande")
@CrossOrigin(origins = "http://localhost:4200")

public class CommandeController {
    private static final Logger logger = LoggerFactory.getLogger(CommandeController.class);

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private ProduitService produitService;


}
