package tn.esprit.ReclamationModule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RendezVous {
    private String rdvId;
    private String nom;
    private int tel;
    private Date dateRdv;
    private  Medecin medecin;

    public RendezVous() {
        this.rdvId = UUID.randomUUID().toString();
    }
}
