package tn.esprit.ReclamationModule.Service;

import org.apache.jena.query.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.springframework.stereotype.Service;
import tn.esprit.ReclamationModule.model.Article;
import tn.esprit.ReclamationModule.model.Commentaire;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentaireService {
    private String sparqlUpdateEndpoint = "http://localhost:3030/mydataset/update";
    private String sparqlQueryEndpoint = "http://localhost:3030/mydataset/query";
    private static final String NAMESPACE = "http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4";


    public void create(Commentaire commentaire) {
        if (commentaire.getArticle() == null || commentaire.getArticle().getId() == null) {
            throw new IllegalArgumentException("The linked article ID must be provided when creating a comment.");
        }
        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + commentaire.getId() + "> r:contenu \"" + escapeSPARQL(commentaire.getContenu()) + "\" ." +
                        "  <" + NAMESPACE + commentaire.getId() + "> r:a_une_reponse <" + NAMESPACE + commentaire.getArticle().getId() + "> ." +
                        "}";

        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
        System.out.println("Created response with ID: " + commentaire.getId() + " for reclamation with ID: " + commentaire.getArticle().getId());
    }

    private String escapeSPARQL(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }


    public List<Commentaire> getAllComments() {
        String queryString =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?responseId ?contenu  ?ArticleId WHERE { " +
                        "  ?commentaireId r:a_une_reponse ?ArticleId . " +
                        "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Commentaire> commentaires = new ArrayList<>();
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Commentaire commentaire = new Commentaire();
            commentaire.setId(solution.getResource("commentaireId").toString());
            commentaire.setContenu(solution.getLiteral("contenu").getString());


            Article article = new Article();
            article.setId(solution.getResource("ArticleId").toString());
            commentaire.setArticle(article);

            commentaires.add(commentaire);
        }
        qexec.close();
        return commentaires;
    }


  /*  public Commentaire getCommentById(String id) {
        String queryString = "PREFIX r: <" + NAMESPACE + "> " +
                "SELECT ?contenu  WHERE { " +
                "  <" + NAMESPACE + id + "> r:contenu ?contenu . " +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);
        Article article = null;
        ResultSet results = qexec.execSelect();

        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            article = new Article();
            article.setId(id);
            article.setTitle(solution.getLiteral("title").getString());
            article.setContenu(solution.getLiteral("contenu").getString());
        } else {
            System.out.println("No article found for ID: " + id);
        }

        qexec.close();
        return article;
    }*/



}
