package tn.esprit.ReclamationModule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class Commentaire {
    private String id;
    private String contenu;
    private Article article; // La relation avec Articles
    public Commentaire() {
        this.id = UUID.randomUUID().toString();
    }

}
