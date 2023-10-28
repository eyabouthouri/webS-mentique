package tn.esprit.ReclamationModule.Service;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.update.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Service;
import tn.esprit.ReclamationModule.model.Reclamation;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReclamationService {
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
    public void create(Reclamation reclamation) {
        if(reclamation.getTitle() == null || reclamation.getDescription() == null) {
            throw new IllegalArgumentException("Title or Description cannot be null");
        }
        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + reclamation.getId() + "> r:title \"" + escapeSPARQL(reclamation.getTitle()) + "\" ." +
                        "  <" + NAMESPACE + reclamation.getId() + "> r:description \"" + escapeSPARQL(reclamation.getDescription()) + "\" ." +
                        "}";
        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
    }


    public List<Reclamation> getAllReclamations() {
        String selectQueryStr =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?id ?title ?description WHERE { " +
                        "  ?id r:title ?title ." +
                        "  ?id r:description ?description ." +
                        "}";

        Query query = QueryFactory.create(selectQueryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<Reclamation> reclamations = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while(results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Reclamation reclamation = new Reclamation();
                reclamation.setId(sol.getResource("id").toString());
                reclamation.setTitle(sol.getLiteral("title").getString());
                reclamation.setDescription(sol.getLiteral("description").getString());
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

    public Reclamation read(String id) {
        String queryString =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?title ?description WHERE { " +
                        "  <" + NAMESPACE + id + "> r:title ?title . " +
                        "  <" + NAMESPACE + id + "> r:description ?description . " +
                        "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        Reclamation reclamation = new Reclamation();
        ResultSet results = qexec.execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            reclamation.setDescription(solution.getLiteral("description").getString());
            reclamation.setTitle(solution.getLiteral("title").getString());
        } else {
            return null;
        }

        return reclamation;
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
