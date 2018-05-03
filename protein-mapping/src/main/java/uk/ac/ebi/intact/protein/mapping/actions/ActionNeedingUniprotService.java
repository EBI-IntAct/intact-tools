package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

/**
 * This class is the class to extend if the IdentificationAction  is using a uniprot service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2010</pre>
 */

public abstract class ActionNeedingUniprotService extends IdentificationActionImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(UniprotNameSearchProcess.class);

    /**
     * The uniprot service
     */
    protected UniProtService uniProtQueryService = Client.getServiceFactoryInstance().getUniProtQueryService();

    public ActionNeedingUniprotService(ReportsFactory factory) {
        super(factory);
    }

    /**
     * Create a query to get the uniprot entries with this organism taxId
     *
     * @param organismName : the organism of the protein to identify
     * @return the query as a Query
     */
    protected Query buildTaxIdQuery(String organismName) {
        return UniParcQueryBuilder.taxonId(Integer.parseInt(organismName));
    }

    /**
     * Add a filter on the Taxid in the initial query
     *
     * @param initialquery : the initial query
     * @param organism     : the organism of the protein
     * @return the query as a Query
     */
    protected Query addTaxIdToQuery(Query initialquery, String organism) {
        return initialquery.and(UniProtQueryBuilder.taxonID(Integer.parseInt(organism)));
    }

    /**
     * Add a filter on the Taxid in the initial query
     *
     * @param initialquery : the initial query
     * @param organism     : the organism of the protein
     * @return the query as a QueryResult<UniProtEntry>
     */
    protected QueryResult<UniProtEntry> addTaxIdToUniprotIterator(QueryResult<UniProtEntry> initialquery, String organism) {
        QueryResult<UniProtEntry> queryResult;
        uniProtQueryService.start();
        try {
            queryResult = uniProtQueryService.getEntries(initialquery.getQuery().and(UniProtQueryBuilder.taxonID(Integer.parseInt(organism))));
            uniProtQueryService.stop();
            return queryResult;
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        uniProtQueryService.stop();
        return null;
    }

    /**
     * Add a filter on the swissprot database in the initial query
     *
     * @param initialquery : the initial query
     * @return the query as a Query
     */
    protected Query addFilterOnSwissprot(Query initialquery) {
        return initialquery.and(UniProtQueryBuilder.swissprot());
    }
}
