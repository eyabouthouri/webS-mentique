package tn.esprit.ReclamationModule.model;

import java.text.SimpleDateFormat;
import java.util.UUID;

import java.util.Date;

public class Reclamation {
    private String id;
    private String title;
    private String description;
    private Date dateSoumission;
    private EtatReclamation etat;




    public EtatReclamation getEtat() {
        return etat;
    }

    public void setEtat(EtatReclamation etat) {
        this.etat = etat;
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateSoumission() {
        return dateSoumission;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateSoumission(Date dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    // Constructeurs
    public Reclamation() {
        this.id = UUID.randomUUID().toString();
        this.dateSoumission = new Date();  // Initialise avec la date actuelle
        this.etat = EtatReclamation.NON_TRAITE;

    }
    public enum EtatReclamation {
        TRAITE,
        NON_TRAITE;
    }

    public Reclamation(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
    public String getDateSoumissionAsString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return formatter.format(dateSoumission);
    }

}
