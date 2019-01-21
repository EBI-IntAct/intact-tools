package uk.ac.ebi.intact.protein.mapping.model.actionReport;

import uk.ac.ebi.intact.protein.mapping.results.UniprotProteinAPICrossReferences;

import java.util.Set;

/**
 * Interface to implement for Uniprot Protein API reports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface UniprotProteinAPIReport<T extends UniprotProteinAPICrossReferences> extends MappingReport{

    public Set<T> getCrossReferences();
    public void addCrossReference(String databaseName, String accession);
    public void addUniprotProteinAPICrossReference(T refs);
}
