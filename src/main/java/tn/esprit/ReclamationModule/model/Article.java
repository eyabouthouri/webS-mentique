package tn.esprit.ReclamationModule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class Article {

    private String id;
    private  String title;
    private String auteur;
    private Date DatePublication;
    private String contenu;
    public Article() {
        this.id = UUID.randomUUID().toString();  // Auto-generate ID using UUID
    }
}
