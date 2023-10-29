package tn.esprit.ReclamationModule.Service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.FileManager;
import org.springframework.stereotype.Service;
import tn.esprit.ReclamationModule.model.Article;
import tn.esprit.ReclamationModule.model.Produit;
import tn.esprit.ReclamationModule.model.Reclamation;
import tn.esprit.ReclamationModule.model.ReclamationDateAscComparator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ArticleService {
    private String sparqlUpdateEndpoint = "http://localhost:3030/mydataset/update";
    private String sparqlQueryEndpoint = "http://localhost:3030/mydataset/query";
    private static final String NAMESPACE = "http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4";

    // Chemin vers le fichier ontologie dans les ressources
    private static final String ONTOLOGY_FILE = "monOntologie.owl";

    // Chargement de l'ontologie depuis le fichier
    private Model ontologyModel;

    @PostConstruct
    public void init() {
        ontologyModel = ModelFactory.createDefaultModel();
        FileManager.get().readModel(ontologyModel, ONTOLOGY_FILE);
    }

    public void create(Article article) {
        if (article.getTitle() == null || article.getContenu() == null) {
            throw new IllegalArgumentException("Title or Description cannot be null");
        }
        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + article.getId() + "> r:title \"" + escapeSPARQL(article.getTitle()) + "\" ." +
                        "  <" + NAMESPACE + article.getId() + "> r:contenu \"" + escapeSPARQL(article.getContenu()) + "\" ." +
                        "}";
        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
    }

    public List<Article> getarticles() {
        String selectQueryStr =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?id ?title ?contenu WHERE { " +
                        "  ?id r:title ?title ." +
                        "  ?id r:contenu ?contenu ." +
                        "}";

        Query query = QueryFactory.create(selectQueryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Article> articles = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while(results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Article article = new Article();
                article.setId(sol.getResource("id").toString());
                article.setTitle(sol.getLiteral("title").getString());
                article.setContenu(sol.getLiteral("contenu").getString());
                articles.add(article);
            }
        } finally {
            qexec.close();
        }

        return articles;
    }
    private String escapeSPARQL(String input) {
        if (input == null) {
            return ""; // or however you want to handle null input
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public Article read(String id) {
        String queryString =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?title ?contenu WHERE { " +
                        "  <" + NAMESPACE + id + "> r:title ?title . " +
                        "  <" + NAMESPACE + id + "> r:contenu ?contenu . " +
                        "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        Article article = new Article();
        ResultSet results = qexec.execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            article.setContenu(solution.getLiteral("contenu").getString());
            article.setTitle(solution.getLiteral("title").getString());
        } else {
            return null;
        }

        return article;
    }
    public void delete(String id) {
        String deleteQueryStr =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "DELETE WHERE { " +
                        "  <" + NAMESPACE + id + "> ?property ?value . " +
                        "}";
        UpdateRequest deleteRequest = UpdateFactory.create(deleteQueryStr);

        // Use the correct SPARQL update endpoint
        UpdateProcessor deleteProcessor = UpdateExecutionFactory.createRemote(deleteRequest, sparqlUpdateEndpoint);

        deleteProcessor.execute();
    }

    public List<Article> searchByTitle(String title) {
        String queryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?id ?title ?description WHERE { " +
                        "  ?id r:title ?title . " +
                        "  ?id r:contenu ?contenu . " +
                        "  FILTER (regex(?title, '" + title + "', 'i'))" +
                        "}";

        Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Article> articles = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Article article = new Article();
                article.setId(sol.getResource("id").toString());
                article.setTitle(sol.getLiteral("title").getString());
                article.setContenu(sol.getLiteral("contenu").getString());
                articles.add(article);
            }
        } finally {
            qexec.close();
        }

        return articles;
    }



}
