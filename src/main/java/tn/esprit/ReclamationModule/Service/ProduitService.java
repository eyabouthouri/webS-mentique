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
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.ReclamationModule.model.Produit;
import tn.esprit.ReclamationModule.model.Reclamation;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class ProduitService {
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

    public void create(Produit produit) {

        Date datePeremption = produit.getDateperemption();


        if (produit.getNom() == null || produit.getDescription() == null) {
            throw new IllegalArgumentException("Title or Description cannot be null");
        }


        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + produit.getId() + "> r:nom \"" + escapeSPARQL(produit.getNom()) + "\" ." +
                        "  <" + NAMESPACE + produit.getId() + "> r:description \"" + escapeSPARQL(produit.getDescription()) + "\" ." +
                        "  <" + NAMESPACE + produit.getId() + "> r:dateperemption \"" + datePeremption + "\" ." +
                        "}";

        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
    }


    public List<Produit> getproduits() {
        String selectQueryStr =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?id ?nom ?description ?dateperemption WHERE { " +
                        "  ?id r:nom ?nom ." +
                        "  ?id r:description ?description ." +
                        "  ?id r:dateperemption ?dateperemption ." +

                        "}";

        Query query = QueryFactory.create(selectQueryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Produit> produits = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Produit produit = new Produit();
                produit.setId(sol.getResource("id").toString());
                produit.setNom(sol.getLiteral("nom").getString());
                produit.setDescription(sol.getLiteral("description").getString());
                String dateAsString = sol.getLiteral("dateperemption").getString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                try {
                    Date date = inputFormat.parse(dateAsString);
                    produit.setDateperemption(date);
                } catch (ParseException e) {
                    // Gérer l'exception si la conversion échoue
                    e.printStackTrace(); // ou autre traitement de l'erreur
                }

                produits.add(produit);
            }
        } finally {
            qexec.close();
        }

        return produits;
    }

    private String escapeSPARQL(String input) {
        if (input == null) {
            return ""; // or however you want to handle null input
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public Produit read(String id) {
        String queryString =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?nom ?description ?dateperemption WHERE { " +
                        "  <" + NAMESPACE + id + "> r:nom ?nom . " +
                        "  <" + NAMESPACE + id + "> r:description ?description . " +
                        "  <" + NAMESPACE + id + "> r:dateperemption ?dateperemption . " +
                        "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        Produit produit = new Produit();
        ResultSet results = qexec.execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            produit.setDescription(solution.getLiteral("description").getString());
            produit.setNom(solution.getLiteral("nom").getString());
        } else {
            return null;
        }

        return produit;
    }


    public List<Produit> getProductByName(String nom) {


        String queryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?id ?nom ?description WHERE { " +
                        "  ?id r:nom ?nom . " +
                        "  ?id r:description ?description . " +
                        "  FILTER (regex(?nom, '" + nom + "', 'i'))" +
                        "}";

        Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Produit> produits = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Produit produit = new Produit();
                produit.setId(sol.getResource("id").toString());
                produit.setNom(sol.getLiteral("nom").getString());
                produit.setDescription(sol.getLiteral("description").getString());
                produits.add(produit);
            }
        } finally {
            qexec.close();
        }

        return produits;


    }


    public List<Produit> getProduitpérimé() {

        Date currentDate = new Date();
        String queryString =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?id ?nom ?description ?dateperemption WHERE { " +
                        "  ?id r:nom ?nom . " +
                        "  ?id r:description ?description . " +
                        "  ?id r:dateperemption ?dateperemption . " +

                        "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Produit> produits = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Produit produit = new Produit();
                produit.setId(sol.getResource("id").toString());
                produit.setNom(sol.getLiteral("nom").getString());
                produit.setDescription(sol.getLiteral("description").getString());
                String dateAsString = sol.getLiteral("dateperemption").getString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                try {
                    Date date = inputFormat.parse(dateAsString);
                    if (date.after(currentDate)) {

                        produits.add(produit);
                    }
                    produit.setDateperemption(date);
                } catch (ParseException e) {
                    // Gérer l'exception si la conversion échoue
                    e.printStackTrace(); // ou autre traitement de l'erreur
                }

               // produits.add(produit);
            }
        } finally {
            qexec.close();
        }

        return produits;


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


}
