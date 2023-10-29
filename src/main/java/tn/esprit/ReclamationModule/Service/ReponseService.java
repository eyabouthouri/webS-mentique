package tn.esprit.ReclamationModule.Service;

import org.apache.jena.query.*;
import org.apache.jena.update.*;
import org.springframework.stereotype.Service;
import tn.esprit.ReclamationModule.model.Reclamation;
import tn.esprit.ReclamationModule.model.Reponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReponseService {

    private String sparqlUpdateEndpoint = "http://localhost:3030/mydataset/update";
    private String sparqlQueryEndpoint = "http://localhost:3030/mydataset/query";
    private static final String NAMESPACE = "http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4";

    public void create(Reponse reponse) {
        if (reponse.getReclamation() == null || reponse.getReclamation().getId() == null) {
            throw new IllegalArgumentException("The linked reclamation ID must be provided when creating a response.");
        }

        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + reponse.getId() + "> r:title \"" + escapeSPARQL(reponse.getTitle()) + "\" ." +
                        "  <" + NAMESPACE + reponse.getId() + "> r:description \"" + escapeSPARQL(reponse.getDescription()) + "\" ." +
                        "  <" + NAMESPACE + reponse.getId() + "> r:a_une_reponse <" + NAMESPACE + reponse.getReclamation().getId() + "> ." +
                        "}";

        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
        System.out.println("Created response with ID: " + reponse.getId() + " for reclamation with ID: " + reponse.getReclamation().getId());
    }

    public Reponse getReponseByReclamationId(String id) {
        String queryString =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?responseId ?title ?description WHERE { " +
                        "  ?responseId r:a_une_reponse <" + NAMESPACE + id + "> ." +
                        "  ?responseId r:title ?title . " +
                        "  ?responseId r:description ?description . " +
                        "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);
        Reponse reponse = null;
        ResultSet results = qexec.execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            reponse = new Reponse();
            reponse.setId(solution.getResource("responseId").toString());
            reponse.setTitle(solution.getLiteral("title").getString());
            reponse.setDescription(solution.getLiteral("description").getString());
            Reclamation reclamation = getReclamationById(id);
            reponse.setReclamation(reclamation);
            reclamation.setId(id);
            reponse.setReclamation(reclamation);
            System.out.println("Fetching response for reclamation with ID: " + id);
        }

        qexec.close();
        return reponse;
    }

    public Reclamation getReclamationById(String id) {
        String queryString = "PREFIX r: <" + NAMESPACE + "> " +
                "SELECT ?title ?description WHERE { " +
                "  <" + NAMESPACE + id + "> r:title ?title . " +
                "  <" + NAMESPACE + id + "> r:description ?description . " +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);
        Reclamation reclamation = null;
        ResultSet results = qexec.execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            reclamation = new Reclamation();
            reclamation.setId(id);
            reclamation.setTitle(solution.getLiteral("title").getString());
            reclamation.setDescription(solution.getLiteral("description").getString());
        }

        qexec.close();
        return reclamation;
    }

    private String escapeSPARQL(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public List<Reponse> getAllReponses() {
        String queryString =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?responseId ?title ?description ?reclamationId WHERE { " +
                        "  ?responseId r:title ?title . " +
                        "  ?responseId r:description ?description . " +
                        "  ?responseId r:a_une_reponse ?reclamationId . " +
                        "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Reponse> reponses = new ArrayList<>();
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Reponse reponse = new Reponse();
            reponse.setId(solution.getResource("responseId").toString());
            reponse.setTitle(solution.getLiteral("title").getString());
            reponse.setDescription(solution.getLiteral("description").getString());

            // Setting reclamation ID
            Reclamation reclamation = new Reclamation();
            reclamation.setId(solution.getResource("reclamationId").toString());
            reponse.setReclamation(reclamation);

            reponses.add(reponse);
        }
        qexec.close();
        return reponses;
    }
}
