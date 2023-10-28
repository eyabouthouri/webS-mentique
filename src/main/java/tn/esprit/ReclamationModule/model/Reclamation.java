package tn.esprit.ReclamationModule.model;

import java.util.UUID;

public class Reclamation {
    private String id;
    private String title;
    private String description;

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

    // Vous pouvez également ajouter des constructeurs si nécessaire.
    public Reclamation() {
        this.id = UUID.randomUUID().toString();  // Auto-generate ID using UUID
    }

    public Reclamation(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
}
