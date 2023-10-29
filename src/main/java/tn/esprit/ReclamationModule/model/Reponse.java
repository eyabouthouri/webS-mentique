package tn.esprit.ReclamationModule.model;

import java.util.UUID;

public class Reponse {
    private String id;
    private String title;
    private String description;
    private Reclamation reclamation; // La relation avec Reclamation

    public Reponse() {
        this.id = UUID.randomUUID().toString();
    }

    public Reponse(String title, String description, Reclamation reclamation) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.reclamation = reclamation;
    }

    public String getId() {
        return id;
    }

    // Note: Generally, it's not a good idea to provide a setter for an ID. IDs should be immutable once set.
    // But if you really want to allow it to change, you can add a setter for it.
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    // ... autres getters, setters, et constructeurs si nécessaire
}
