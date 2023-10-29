package tn.esprit.ReclamationModule.Service;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.update.*;
import org.apache.jena.util.FileManager;
import org.springframework.stereotype.Service;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import tn.esprit.ReclamationModule.model.Medecin;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
@Service

public class MedecinService {
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
    public void create(Medecin medecin) {
        if(medecin.getNom() == null || medecin.getPrenom() == null) {
            throw new IllegalArgumentException("Nom or prenom cannot be null");
        }
        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + medecin.getMedecinId() + "> r:nom \"" + escapeSPARQL(medecin.getNom()) + "\" ." +
                        "  <" + NAMESPACE + medecin.getMedecinId() + "> r:prenom \"" + escapeSPARQL(medecin.getPrenom()) + "\" ." +
                        "  <" + NAMESPACE + medecin.getMedecinId() + "> r:adresse \"" + escapeSPARQL(medecin.getAdresse()) + "\" ." +
                            "  <" + NAMESPACE + medecin.getMedecinId() + "> r:specialite \"" + escapeSPARQL(medecin.getSpecialite()) + "\" ." +
                        "  <" + NAMESPACE + medecin.getMedecinId() + "> r:tel \"" + escapeSPARQL(String.valueOf(medecin.getTel())) + "\" ." +

                        "}";
        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
    }


    public List<Medecin> getAllMedecins() {
        String selectQueryStr =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?medecinId ?nom ?prenom ?adresse ?specialite ?tel WHERE { " +
                        "  ?medecinId r:nom ?nom ." +
                        "  ?medecinId r:prenom ?prenom ." +
                        "  ?medecinId r:adresse ?adresse ." +
                        "  ?medecinId r:specialite ?specialite ." +
                        "  ?medecinId r:tel ?tel ." +
                        "}";

        Query query = QueryFactory.create(selectQueryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Medecin> reclamations = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while(results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Medecin reclamation = new Medecin();
                reclamation.setMedecinId(sol.getResource("medecinId").toString());
                reclamation.setNom(sol.getLiteral("nom").getString());
                reclamation.setPrenom(sol.getLiteral("prenom").getString());
                reclamation.setAdresse(sol.getLiteral("adresse").getString());
                reclamation.setSpecialite(sol.getLiteral("specialite").getString());
                reclamation.setTel(sol.getLiteral("tel").getInt());

                reclamations.add(reclamation);
            }
        } finally {
            qexec.close();
        }

        return reclamations;
    }
    private String escapeSPARQL(String input) {
        if(input == null) {
            return ""; // or however you want to handle null input
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public Medecin read(String id) {
        String queryString =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?nom ?prenom ?adresse ?specialite ?tel WHERE { " +
                        "  <" + NAMESPACE + id + "> r:nom ?nom . " +
                        "  <" + NAMESPACE + id + "> r:prenom ?prenom . " +
                        "  <" + NAMESPACE + id + "> r:adresse ?adresse . " +
                        "  <" + NAMESPACE + id + "> r:specialite ?specialite . " +
                        "  <" + NAMESPACE + id + "> r:tel ?tel . " +
                        "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        ResultSet results = qexec.execSelect();

        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Medecin reclamation = new Medecin();
            reclamation.setNom(solution.getLiteral("nom").getString());
            reclamation.setPrenom(solution.getLiteral("prenom").getString());
            reclamation.setAdresse(solution.getLiteral("adresse").getString());
            reclamation.setSpecialite(solution.getLiteral("specialite").getString());
            reclamation.setTel(solution.getLiteral("tel").getInt());

            return reclamation;
        }

        return null;
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


    public Medecin getMedecinById(String medecinId) {
        String selectQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?nom ?prenom ?adresse ?specialite ?tel WHERE { " +
                        "  <" + NAMESPACE + medecinId + "> r:nom ?nom ." +
                        "  <" + NAMESPACE + medecinId + "> r:prenom ?prenom ." +
                        "  <" + NAMESPACE + medecinId + "> r:adresse ?adresse ." +
                        "  <" + NAMESPACE + medecinId + "> r:specialite ?specialite ." +
                        "  <" + NAMESPACE + medecinId + "> r:tel ?tel ." +
                        "}";

        Query query = QueryFactory.create(selectQueryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        Medecin medecin = new Medecin(); // Create a Medecin instance to populate

        try {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                medecin.setNom(sol.getLiteral("nom").getString());
                medecin.setPrenom(sol.getLiteral("prenom").getString());
                medecin.setAdresse(sol.getLiteral("adresse").getString());
                medecin.setSpecialite(sol.getLiteral("specialite").getString());
                medecin.setTel(sol.getLiteral("tel").getInt());
            }
        } finally {
            qexec.close();
        }

        return medecin;
    }
}
