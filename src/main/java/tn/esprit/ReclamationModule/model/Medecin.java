package tn.esprit.ReclamationModule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class Medecin {
    private String medecinId;
    private String nom;
    private String prenom;
    private String adresse;
    private String specialite;
    private int tel;

    public Medecin() {
        this.medecinId = UUID.randomUUID().toString();  // Auto-generate ID using UUID
    }



}
