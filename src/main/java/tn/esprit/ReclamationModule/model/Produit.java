package tn.esprit.ReclamationModule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class Produit {
    private String id;
    private  String nom;
    private String description;
    private Date dateFabrication;
    private Date dateperemption;
    public Produit() {
        this.id = UUID.randomUUID().toString();  // Auto-generate ID using UUID
    }

}
