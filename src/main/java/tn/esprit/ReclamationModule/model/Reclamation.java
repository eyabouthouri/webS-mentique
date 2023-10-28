package tn.esprit.ReclamationModule.model;

import java.util.UUID;

import java.util.Date;

public class Reclamation {
    private String id;
    private String title;
    private String description;
    private Date dateSoumission;

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
    }

    public Reclamation(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
}
