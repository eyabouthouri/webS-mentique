package tn.esprit.ReclamationModule.Service;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.ReclamationModule.model.Commande;
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
public class CommandeService {
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

    public void create(Commande commande) {

        if (commande.getProduit() == null || commande.getProduit().getId() == null) {
            throw new IllegalArgumentException("product not found ");
        }
        String insertQueryStr =
                "PREFIX r: <" + NAMESPACE + "> " +
                        "INSERT DATA { " +
                        "  <" + NAMESPACE + commande.getId() + "> r:nom \"" + escapeSPARQL(commande.getNom()) + "\" ." +
                        "  <" + NAMESPACE + commande.getId() + "> r:prenom \"" + escapeSPARQL(commande.getPrenom()) + "\" ." +
                        "  <" + NAMESPACE + commande.getId() + "> r:passer_une_commande <" + NAMESPACE + commande.getProduit().getId() + "> ." +
                        "}";

        UpdateRequest insertRequest = UpdateFactory.create(insertQueryStr);
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(insertRequest, sparqlUpdateEndpoint);
        upp.execute();
        System.out.println("Created response with ID: " + commande.getId() + " for reclamation with ID: " + commande.getProduit().getId());
    }
    private String escapeSPARQL(String input) {
        if (input == null) {
            return ""; // or however you want to handle null input
        }
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    public List<Commande> getAllCommande() {
        String queryStr =
                "PREFIX r: <http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4> " +
                        "SELECT ?id ?nom ?prenom ?produit  WHERE { " +
                        "  ?id r:nom ?nom ." +
                        "  ?id r:prenom ?prenom ." +
                        "  ?id r:passer_une_commande ?produit ." +


                        "}";

        List<Commande> commandes = new ArrayList<>();

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlQueryEndpoint, queryStr)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Commande commande = new Commande();
                commande.setId(sol.getResource("id").toString());
                commande.setNom(sol.getLiteral("nom").getString());
                commande.setPrenom(sol.getLiteral("prenom").getString());

                // Get the associated product for the command
                Resource productResource = sol.getResource("produit");
                Produit associatedProduct = convertToProduitClass(productResource);
             //   String productName = sol.getLiteral("nomproduit").getString();

             //   associatedProduct.setNom(productName);

                commande.setProduit(associatedProduct);
                commandes.add(commande);
            }
        }

        return commandes;
    }

    private Produit convertToProduitClass(Resource productResource) {
        Produit produit = new Produit();

        if (productResource != null) {
            if (productResource.hasProperty(RDF.type, productResource.getModel().getResource("http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4#produit"))) {
                if (productResource.hasProperty(productResource.getModel().getProperty("http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4#nom"))) {
                    String nom = productResource.getProperty(productResource.getModel().getProperty("http://www.semanticweb.org/dorsaf/ontologies/2023/9/untitled-ontology-4#nom")).getString();
                    produit.setNom(nom);
                }
                // Autres propriétés du produit
            }
        }

        return produit;
    }


}
