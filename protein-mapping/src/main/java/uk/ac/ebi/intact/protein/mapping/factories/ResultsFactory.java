package uk.ac.ebi.intact.protein.mapping.factories;

import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.results.UniprotProteinAPICrossReferences;

/**
 * Interface for factories returning results of protein mapping
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface ResultsFactory {

    public BlastResults getBlastResults();
    public IdentificationResults getIdentificationResults();
    public UniprotProteinAPICrossReferences getUniprotProteinAPICrossReferences();
}
