package tn.esprit.ReclamationModule.Service;
import tn.esprit.ReclamationModule.model.Medecin;
import org.apache.jena.query.*;
import org.apache.jena.update.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import tn.esprit.ReclamationModule.model.RendezVous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
// Add appropriate annotations for a Spring service
public class RdvService {
    private static final Logger logger = LoggerFactory.getLogger(RdvService.class);

    private String sparqlUpdateEndpoint = "http://localhost:3030/mydataset/update";
    private String sparqlQueryEndpoint = "http://localhost:3030/mydataset/query";
    private static final String NAMESPACE = "http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4";

    // Create RDF prefixes and other constants as needed

    public void create(RendezVous rendezVous) {
        if (rendezVous.getMedecin() == null || rendezVous.getMedecin().getMedecinId() == null) {
            throw new IllegalArgumentException("The linked reclamation ID must be provided when creating a response.");
        }

        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + rendezVous.getRdvId() + "> r:nom \"" + escapeSPARQL(rendezVous.getNom()) + "\" ." +
                        "  <" + NAMESPACE + rendezVous.getRdvId() + "> r:a_un_medecin <" + NAMESPACE + rendezVous.getMedecin().getMedecinId() + "> ." +
                        "}";
        System.out.println(""+insertQueryStr);
        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
        System.out.println("Created response with ID: " + rendezVous.getRdvId() + " for reclamation with ID: " + rendezVous.getMedecin().getMedecinId());
    }


    private String escapeSPARQL(String input) {
        if(input == null) {
            return ""; // or however you want to handle null input
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public List<RendezVous> getAllRdvs(String medecinId) {
        String selectQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?rdvId ?nom ?medecinId WHERE { " +
                        "  ?rdvId r:nom ?nom . " +
                        "  ?rdvId r:a_un_medecin <" + NAMESPACE + medecinId + "> ." +
                        "}";


        Query query = QueryFactory.create(selectQueryStr);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        List<RendezVous> rdvs = new ArrayList<>();

        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                RendezVous rdv = new RendezVous();
                rdv.setRdvId(sol.getResource("rdvId").toString());
                rdv.setNom(sol.getLiteral("nom").getString());

                Medecin medecin = getMedecinById(medecinId);
                rdv.setMedecin(medecin);
                medecin.setMedecinId(medecinId);
                rdv.setMedecin(medecin);
                rdvs.add(rdv);
            }
        } finally {
            qexec.close();
        }

        return rdvs;
    }

    public Medecin getMedecinById(String id) {
        String queryString = "PREFIX r: <" + NAMESPACE + "> " +
                "SELECT ?nom ?prenom ?adresse ?specialite WHERE { " +
                "  <" + NAMESPACE + id + "> r:nom ?nom . " +
                "  <" + NAMESPACE + id + "> r:prenom ?prenom . " +
                "  <" + NAMESPACE + id + "> r:adresse ?adresse . " +
                "  <" + NAMESPACE + id + "> r:specialite ?specialite . " +

                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);
        Medecin reclamation = null;
        ResultSet results = qexec.execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            reclamation = new Medecin();
            reclamation.setMedecinId(id);
            reclamation.setNom(solution.getLiteral("nom").getString());
            reclamation.setPrenom(solution.getLiteral("prenom").getString());
            reclamation.setAdresse(solution.getLiteral("adresse").getString());
            reclamation.setSpecialite(solution.getLiteral("specialite").getString());

        }

        qexec.close();
        return reclamation;
    }
    }
