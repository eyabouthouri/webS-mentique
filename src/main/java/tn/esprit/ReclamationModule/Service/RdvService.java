package tn.esprit.ReclamationModule.Service;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import tn.esprit.ReclamationModule.model.Medecin;
import org.apache.jena.query.*;
import org.apache.jena.update.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import tn.esprit.ReclamationModule.model.RendezVous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            throw new IllegalArgumentException("The linked Medecin ID must be provided when creating a rdv.");
        }

        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + rendezVous.getRdvId() + "> r:nom \"" + escapeSPARQL(rendezVous.getNom()) + "\" ." +
                        "  <" + NAMESPACE + rendezVous.getRdvId() + "> r:tel \"" + escapeSPARQL(String.valueOf(rendezVous.getTel())) + "\" ." +
                        "  <" + NAMESPACE + rendezVous.getRdvId() + "> r:dateRdv \"" + escapeSPARQL(String.valueOf(rendezVous.getDateRdv())) + "\" ." +
                        "  <" + NAMESPACE + rendezVous.getRdvId() + "> r:a_un_medecin <" + NAMESPACE + rendezVous.getMedecin().getMedecinId() + "> ." +
                        "}";
        System.out.println(""+insertQueryStr);
        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
        System.out.println("Created Rdv with ID: " + rendezVous.getRdvId() + " for Medecin with ID: " + rendezVous.getMedecin().getMedecinId());
    }

    public List<Date> getExistingDatesForMedecin(String medecinId) {
        List<Date> existingDates = new ArrayList<>();

        // Créez la requête SPARQL
        String queryString =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "SELECT ?dateRdv " +
                        "WHERE { " +
                        "  ?rdvId r:a_un_medecin <" + NAMESPACE + medecinId + "> ; " +
                        "       r:dateRdv ?dateRdv . " +
                        "}";

        // Créez un objet QueryExecution pour exécuter la requête
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, query);

        try {
            // Exécutez la requête et obtenez un ResultSet
            ResultSet results = qexec.execSelect();

            // Parcourez les résultats et ajoutez les dates existantes à la liste
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                RDFNode dateNode = sol.get("dateRdv");
                if (dateNode.isLiteral()) {
                    Literal dateLiteral = dateNode.asLiteral();
                    // Convertissez la valeur de dateLiteral en objet Date
                    Date date = yourConversionMethod(dateLiteral.getString());
                    existingDates.add(date);
                }
            }
        } finally {
            qexec.close();
        }

        return existingDates;
    }
    private Date yourConversionMethod(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            // Handle the exception (e.g., log it or throw a custom exception)
            e.printStackTrace();
            return null;
        }
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
                        "SELECT ?rdvId ?nom ?tel ?dateRdv ?medecinId WHERE { " +
                        "  ?rdvId r:nom ?nom . " +
                        "  ?rdvId r:tel ?tel . " +
                        "  ?rdvId r:dateRdv ?dateRdv . " +
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
                rdv.setTel(sol.getLiteral("tel").getInt());
                String dateLiteral = sol.getLiteral("dateRdv").getString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                Date dateRdv = dateFormat.parse(dateLiteral);

                rdv.setDateRdv(dateRdv);

                Medecin medecin = getMedecinById(medecinId);
                rdv.setMedecin(medecin);
                medecin.setMedecinId(medecinId);
                rdv.setMedecin(medecin);
                rdvs.add(rdv);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            qexec.close();
        }

        return rdvs;
    }

    public Medecin getMedecinById(String id) {
        String queryString = "PREFIX r: <" + NAMESPACE + "> " +
                "SELECT ?nom ?prenom ?adresse ?tel ?specialite WHERE { " +
                "  <" + NAMESPACE + id + "> r:nom ?nom . " +
                "  <" + NAMESPACE + id + "> r:prenom ?prenom . " +
                "  <" + NAMESPACE + id + "> r:adresse ?adresse . " +
                "  <" + NAMESPACE + id + "> r:specialite ?specialite . " +
                "  <" + NAMESPACE + id + "> r:tel ?tel . " +

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
            reclamation.setTel(solution.getLiteral("tel").getInt());

        }

        qexec.close();
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
