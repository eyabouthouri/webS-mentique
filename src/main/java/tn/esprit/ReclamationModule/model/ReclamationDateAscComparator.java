package tn.esprit.ReclamationModule.model;

import java.util.Comparator;

public class ReclamationDateAscComparator implements Comparator<Reclamation> {
    @Override
    public int compare(Reclamation r1, Reclamation r2) {
        // Compare les dates en utilisant la méthode compareTo de la classe Date
        return r1.getDateSoumission().compareTo(r2.getDateSoumission());
    }
}