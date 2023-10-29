package tn.esprit.ReclamationModule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Commande {
    private String id;
    private String  nom;
  private String prenom;
  private Produit produit;
    public Commande() {
        this.id = UUID.randomUUID().toString();  // Auto-generate ID using UUID
    }

}
