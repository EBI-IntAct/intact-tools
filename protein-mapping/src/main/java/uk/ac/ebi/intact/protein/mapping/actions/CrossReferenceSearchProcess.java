package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.SimpleUniprotRemoteService;
import uk.ac.ebi.intact.uniprot.service.UniprotService;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.ReflectionCrossReferenceBuilder;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.UniprotCrossReference;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.QueryResultPage;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcService;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;

import java.util.*;

/**
 * This class is querying uniprot to retrieve cross references and identifiers of an uniprot entry.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2010</pre>
 */

public class CrossReferenceSearchProcess extends ActionNeedingUniprotService {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(CrossReferenceSearchProcess.class);
    /**
     * The swissprot database name in uniparc
     */
    private static final String swissprot = "UniProtKB/Swiss-Prot";
    /**
     * The swissprot database name for splice variants in uniparc
     */
    private static final String swissprot_sv = "uniprotkb/swiss-prot protein isoforms";
    /**
     * The uniprot remote service
     */
    private static UniprotService uniprotService;
    /**
     * the uniparc service
     */
    private UniParcService uniParcQueryService = Client.getServiceFactoryInstance().getUniParcQueryService();
    /**
     * The map allowing to convert a sequence database MI to a Uniprot database name
     */
    private Map<String, Set<DatabaseType>> psiMIDatabaseToUniprot = new HashMap<String, Set<DatabaseType>>();
    /**
     * The map allowing to convert a sequence database name to a Uniprot database name
     */
    private Map<String, Set<DatabaseType>> psiDatabaseToUniprot = new HashMap<String, Set<DatabaseType>>();

    /**
     * Create a new CrossReferenceSearchProcess and initialises the list of databases in uniprot with a MI number
     */
    public CrossReferenceSearchProcess(ReportsFactory factory) {
        super(factory);
        initialisePsiMIDatabaseToUniprot();
        uniprotService = new SimpleUniprotRemoteService();
    }

    public CrossReferenceSearchProcess(ReportsFactory factory, UniprotService service) {
        super(factory);
        initialisePsiMIDatabaseToUniprot();
        uniprotService = service != null ? service : new SimpleUniprotRemoteService();
    }

    /**
     * initialises the list of databases in uniprot with a MI number
     */
    private void initialisePsiMIDatabaseToUniprot() {
        HashSet<DatabaseType> CYGD = new HashSet<DatabaseType>();
        CYGD.add(DatabaseType.CYGD);
        psiMIDatabaseToUniprot.put("MI:0464", CYGD);
        psiDatabaseToUniprot.put("cygd", CYGD);
        HashSet<DatabaseType> ddbjEmblGenbank = new HashSet<DatabaseType>();
        ddbjEmblGenbank.add(DatabaseType.getDatabaseType("DDBJ"));
        ddbjEmblGenbank.add(DatabaseType.EMBL);
        ddbjEmblGenbank.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0475", ddbjEmblGenbank);
        psiDatabaseToUniprot.put("ddbj/embl/genbank", ddbjEmblGenbank);
        HashSet<DatabaseType> ensembl = new HashSet<DatabaseType>();
        ensembl.add(DatabaseType.ENSEMBL);
        psiMIDatabaseToUniprot.put("MI:0476", ensembl);
        psiDatabaseToUniprot.put("ensembl", ensembl);
        HashSet<DatabaseType> geneId = new HashSet<DatabaseType>();
        geneId.add(DatabaseType.GENEID);
        psiMIDatabaseToUniprot.put("MI:0477", geneId);
        psiDatabaseToUniprot.put("entrezgene/locuslink", geneId);
        HashSet<DatabaseType> flyBase = new HashSet<DatabaseType>();
        flyBase.add(DatabaseType.FLYBASE);
        psiMIDatabaseToUniprot.put("MI:0478", flyBase);
        psiDatabaseToUniprot.put("flybase", flyBase);
        HashSet<DatabaseType> mgi = new HashSet<DatabaseType>();
        mgi.add(DatabaseType.MGI);
        psiMIDatabaseToUniprot.put("MI:0479", mgi);
        psiDatabaseToUniprot.put("mgd/mgi", mgi);
        HashSet<DatabaseType> mim = new HashSet<DatabaseType>();
        mim.add(DatabaseType.MIM);
        psiMIDatabaseToUniprot.put("MI:0480", mim);
        psiDatabaseToUniprot.put("omim", mim);
        HashSet<DatabaseType> refSeq = new HashSet<DatabaseType>();
        refSeq.add(DatabaseType.REFSEQ);
        psiMIDatabaseToUniprot.put("MI:0481", refSeq);
        psiDatabaseToUniprot.put("refseq", refSeq);
        HashSet<DatabaseType> rgd = new HashSet<DatabaseType>();
        rgd.add(DatabaseType.RGD);
        psiMIDatabaseToUniprot.put("MI:0483", rgd);
        psiDatabaseToUniprot.put("rgd", rgd);
        HashSet<DatabaseType> sgd = new HashSet<DatabaseType>();
        sgd.add(DatabaseType.SGD);
        psiMIDatabaseToUniprot.put("MI:0484", sgd);
        psiDatabaseToUniprot.put("sgd", sgd);
        HashSet<DatabaseType> wormbase = new HashSet<DatabaseType>();
        wormbase.add(DatabaseType.WORMBASE);
        psiMIDatabaseToUniprot.put("MI:0487", wormbase);
        psiDatabaseToUniprot.put("wormbase", wormbase);
        HashSet<DatabaseType> ipi = new HashSet<DatabaseType>();
        ipi.add(DatabaseType.IPI);
        psiMIDatabaseToUniprot.put("MI:0675", ipi);
        psiDatabaseToUniprot.put("ipi", ipi);
        HashSet<DatabaseType> peptide = new HashSet<DatabaseType>();
        peptide.add(DatabaseType.PRIDE);
        peptide.add(DatabaseType.PEPTIDEATLAS);
        psiMIDatabaseToUniprot.put("MI:0737", peptide);
        psiDatabaseToUniprot.put("pep seq db", peptide);
        HashSet<DatabaseType> pride = new HashSet<DatabaseType>();
        pride.add(DatabaseType.PRIDE);
        psiMIDatabaseToUniprot.put("MI:0738", pride);
        psiDatabaseToUniprot.put("pride", pride);
        HashSet<DatabaseType> peptideAtlas = new HashSet<DatabaseType>();
        peptideAtlas.add(DatabaseType.PEPTIDEATLAS);
        psiMIDatabaseToUniprot.put("MI:0741", peptideAtlas);
        psiDatabaseToUniprot.put("peptide atlas", peptideAtlas);
        HashSet<DatabaseType> genBank = new HashSet<DatabaseType>();
        genBank.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0851", genBank);
        psiDatabaseToUniprot.put("genbank_protein_gi", genBank);
        HashSet<DatabaseType> genBank2 = new HashSet<DatabaseType>();
        genBank2.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0860", genBank2);
        psiDatabaseToUniprot.put("genbank indentifier", genBank2);
        HashSet<DatabaseType> genBank3 = new HashSet<DatabaseType>();
        genBank3.add(DatabaseType.getDatabaseType("GenBank"));
        psiMIDatabaseToUniprot.put("MI:0852", genBank3);
        psiDatabaseToUniprot.put("genbank_nucl_gi", genBank3);
        HashSet<DatabaseType> ensemblgenome = new HashSet<DatabaseType>();
        ensemblgenome.add(DatabaseType.ENSEMBLBACTERIA);
        ensemblgenome.add(DatabaseType.ENSEMBLFUNGI);
        ensemblgenome.add(DatabaseType.ENSEMBLMETAZOA);
        ensemblgenome.add(DatabaseType.ENSEMBLPLANTS);
        ensemblgenome.add(DatabaseType.ENSEMBLPROTISTS);
        psiMIDatabaseToUniprot.put("MI:1013", ensemblgenome);
        psiDatabaseToUniprot.put("ensemblgenomes", ensemblgenome);
    }

    /**
     * @param database : the database
     * @return the appropriate name(s) in uniprot if database is a MI number of a sequence database existing in Uniprot, the database name as it was otherwise
     */
    private Set<DatabaseType> convertMINumberInUniprot(String database, String databaseName) {

        Set<DatabaseType> name = new HashSet<DatabaseType>();

        if (database == null && databaseName == null) {
            return name;
        }
        if (database != null && this.psiMIDatabaseToUniprot.containsKey(database)) {
            name.addAll(this.psiMIDatabaseToUniprot.get(database));
        }
        if (databaseName != null && this.psiDatabaseToUniprot.containsKey(databaseName)) {
            name.addAll(this.psiDatabaseToUniprot.get(databaseName));
        }

        return name;
    }

    /**
     * Build the query in uniprot to get the swissprot entries with this identifier in their cross references
     *
     * @param identifier : the identifier of the protein we want to identify
     * @return the iterator
     */
    private QueryResult<UniProtEntry> getReviewedUniprotDatabaseCrossReferenceIterator(Set<DatabaseType> databases, String identifier) {
        QueryResult<UniProtEntry> iteratorId = null;
        uniProtQueryService.start();
        try {
            iteratorId = uniProtQueryService.getEntries(UniProtQueryBuilder.swissprot().and(UniProtQueryBuilder.id(identifier)));
        } catch (ServiceException e) {
            uniProtQueryService.stop();
            e.printStackTrace();
        }
        QueryResult<UniProtEntry> iteratorUniprotXRef = null;

        if (databases.isEmpty()) {
            uniProtQueryService.stop();
            return iteratorId;
        }

        for (DatabaseType database : databases) {

            if (database != null) {
                QueryResult<UniProtEntry> singleDtabase = null;
                try {
                    singleDtabase = uniProtQueryService.getEntries(UniProtQueryBuilder.reviewed(UniProtQueryBuilder.xref(database, identifier), true));
                } catch (ServiceException e) {
                    uniProtQueryService.stop();
                    e.printStackTrace();
                }
                if (singleDtabase != null && iteratorUniprotXRef == null) {
                    iteratorUniprotXRef = singleDtabase;
                } else {
                    if (singleDtabase != null) {
                        try {
                            iteratorUniprotXRef = uniProtQueryService.getEntries(iteratorUniprotXRef.getQuery().or(singleDtabase.getQuery()));
                        } catch (ServiceException e) {
                            uniProtQueryService.stop();
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (iteratorId != null && iteratorUniprotXRef != null) {
            try {
                QueryResult<UniProtEntry> queryResult = uniProtQueryService.getEntries(iteratorId.getQuery().or(iteratorUniprotXRef.getQuery()));
                uniProtQueryService.stop();
                return queryResult;
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
        }
        uniProtQueryService.stop();
        return null;
    }

    /**
     * Build the query in uniprot to get the uniprot entries with this identifier in their cross references
     *
     * @param identifier : the identifier of the protein we want to identify
     * @return the iterator
     */
    private QueryResult<UniProtEntry> getUnreviewedUniprotDatabaseCrossReferenceIterator(Set<DatabaseType> databases, String identifier) {
        QueryResult<UniProtEntry> iteratorId = null;
        uniProtQueryService.start();
        try {
            iteratorId = uniProtQueryService.getEntries(UniProtQueryBuilder.reviewed(UniProtQueryBuilder.id(identifier), false));
        } catch (ServiceException e) {
            uniProtQueryService.stop();
            e.printStackTrace();
        }

        QueryResult<UniProtEntry> iteratorUniprotXRef = null;

        if (databases.isEmpty()) {
            uniProtQueryService.stop();
            return iteratorId;
        }

        for (DatabaseType database : databases) {

            if (database != null) {
                QueryResult<UniProtEntry> singleDtabase = null;
                try {
                    singleDtabase = uniProtQueryService.getEntries(UniProtQueryBuilder.reviewed(UniProtQueryBuilder.xref(database, identifier), false));
                } catch (ServiceException e) {
                    uniProtQueryService.stop();
                    e.printStackTrace();
                }

                if (iteratorUniprotXRef == null) {
                    iteratorUniprotXRef = singleDtabase;
                } else {
                    try {
                        iteratorUniprotXRef = uniProtQueryService.getEntries(iteratorUniprotXRef.getQuery().or(iteratorUniprotXRef.getQuery()));
                    } catch (ServiceException e) {
                        uniProtQueryService.stop();
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            if (iteratorId != null && iteratorUniprotXRef != null) {
                QueryResult<UniProtEntry> queryResult = uniProtQueryService.getEntries(iteratorId.getQuery().or(iteratorUniprotXRef.getQuery()));
                uniProtQueryService.stop();
                return queryResult;
            }
        } catch (ServiceException e) {
            uniProtQueryService.stop();
            e.printStackTrace();
        }
        uniProtQueryService.stop();
        return null;
    }

    /**
     * Build the query in uniparc to get the uniparc entries with this identifier in their cross references
     *
     * @param identifier : the identifier of the protein we want to identify
     * @return the iterator
     */
    private QueryResult<UniParcEntry> getUniparcDatabaseCrossReferenceIterator(String identifier) {
        QueryResult<UniParcEntry> iteratorId = null;
        uniParcQueryService.start();
        try {
            iteratorId = uniParcQueryService.getEntries(UniParcQueryBuilder.id(identifier));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        QueryResult<UniParcEntry> iteratorUniparcXRef = null;
        try {
            iteratorUniparcXRef = uniParcQueryService.getEntries(UniParcQueryBuilder.uniProtAccession(identifier));
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        if (iteratorId != null && iteratorUniparcXRef != null) {
            try {
                QueryResult<UniParcEntry> queryResult = uniParcQueryService.getEntries(iteratorId.getQuery().or(iteratorUniparcXRef.getQuery()));
                uniParcQueryService.stop();
                return queryResult;
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }

        uniParcQueryService.stop();
        return null;
    }

    /**
     * @param protein    : the protein returned by the query
     * @param identifier : the identifier
     * @return true if there is an exact exact accession with this identifier
     */
    private boolean hasTheExactIdentifierInUniprot(UniProtEntry protein, String identifier) {

        if (protein.getPrimaryUniProtAccession().getValue().equals(identifier)) {
            return true;
        }

        if (!protein.getSecondaryUniProtAccessions().isEmpty()) {
            for (SecondaryUniProtAccession ac : protein.getSecondaryUniProtAccessions()) {
                if (ac.getValue() != null && ac.getValue().equals(identifier)) {
                    return true;
                }
            }
        }
        return protein.getUniProtId().getValue().equals(identifier);

    }

    /**
     * @param protein    : the protein returned by the query
     * @param identifier : the identifier
     * @param database   : the database name of MI
     * @return the database name in Uniprot if there is an exact cross reference with this identifier
     */
    private String getTheDatabaseOfExactIdentifierInUniprotCrossReferences(UniProtEntry protein, String identifier, String database) {

        Collection<DatabaseCrossReference> databaseCrossReferences = protein.getDatabaseCrossReferences();
        ReflectionCrossReferenceBuilder builder = new ReflectionCrossReferenceBuilder();

        for (DatabaseCrossReference ref : databaseCrossReferences) {
            Collection<UniprotCrossReference> urefs = builder.build(ref);

            for (UniprotCrossReference uref : urefs) {
                if (uref.getAccessionNumber().equals(identifier)) {
                    String databaseUniprot = uref.getDatabase();

                    return databaseUniprot;
                }
            }
        }

        return null;
    }

    /**
     * @param protein    : the protein returned by the query
     * @param identifier : the identifier
     * @return the database name if there is an exact uniparc cross reference with this identifier
     */
    private String getTheDatabaseOfExactIdentifierInUniparcCrossReferences(UniParcEntry protein, String identifier) {

        Set<uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference> databaseCrossReferences = protein.getDatabaseCrossReferences();

        for (uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference ref : databaseCrossReferences) {

            if (ref.getAccession().equals(identifier)) {
                String databaseName = ref.getDatabase().getName();
                return databaseName;
            }
        }

        return null;
    }

    /**
     * @param protein    : the protein returned by the query
     * @param identifier : the identifier
     * @return true if there is an exact uniparc accession with this identifier
     */
    private boolean hasTheExactIdentifierInUniparc(UniParcEntry protein, String identifier) {

        return protein.getUniParcId().getValue().equals(identifier);
    }

    /**
     * @param uniprot : the uniprot accession
     * @param taxId   : the taxId
     * @return true if the uniprot entry with this accession matches the taxId
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    private boolean hasTheAppropriateOrganism(String uniprot, String taxId) throws ActionProcessingException {

        if (taxId != null) {
            uniprotService.start();
            Collection<UniprotProtein> proteins = uniprotService.retrieve(uniprot);

            if (proteins.size() != 1) {
                uniprotService.close();
                throw new ActionProcessingException("The uniprot accession " + uniprot + " could match several uniprot entries.");
            } else if (proteins.isEmpty()) {
                uniprotService.close();
                throw new ActionProcessingException("The uniprot accession " + uniprot + " couldn't match any uniprot entries.");
            } else {
                UniprotProtein prot = proteins.iterator().next();

                if (prot.getOrganism() != null) {
                    String tax = Integer.toString(prot.getOrganism().getTaxid());
                    if (taxId.equalsIgnoreCase(tax)) {
                        return true;
                    }
                }
            }
        } else {
            uniprotService.close();
            return true;
        }
        uniprotService.close();
        return false;
    }

    /**
     * Process the results of the query. If only one entry is matching, the query was successful, if several entries were matching,
     * the results should be reviewed by a curator
     *
     * @param iterator : the results
     * @param report   : the current report
     * @param context  : the context of the protein
     * @return an unique uniprot accession if successful, null otherwise
     */
    private String processIterator(QueryResult<UniProtEntry> iterator, MappingReport report, IdentificationContext context) {
        // we have only one entry
        if (iterator.getNumberOfHits() == 1) {
            UniProtEntry protein = iterator.next();
            String id = protein.getPrimaryUniProtAccession().getValue();

            Status status = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id);
            report.setStatus(status);
            return id;
        }
        // we have several matching entries
        else if (iterator.getNumberOfHits() > 1) {

            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + iterator.getNumberOfHits() + " Uniprot entries.");
            report.setStatus(status);
            report.setIsASwissprotEntry(false);
        }
        return null;
    }

    /**
     * process the Uniparc entry to extract its uniprot cross references if any
     *
     * @param entry                    : the uniparc entry
     * @param taxId                    : the taxId of the protein we want to retrieve
     * @param setOfSwissprotAccessions : the set of Swissprot cross references in the uniparc results
     * @param setOfUniprotAccessions   : the set of Trembl cross references in the uniparc results
     * @throws ActionProcessingException
     */
    private void processUniparcResult(UniParcEntry entry, String taxId, Set<String> setOfSwissprotAccessions, Set<String> setOfUniprotAccessions) throws ActionProcessingException {
        // get the uniparc cross references
        Set<uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference> uniprotRefs = entry.getUniProtDatabaseCrossReferences();
        if (!uniprotRefs.isEmpty()) {
            for (uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference uniprotRef : uniprotRefs) {
                // if the taxId is matching the uniprot cross references
                if (hasTheAppropriateOrganism(uniprotRef.getAccession(), taxId)) {

                    String databaseName = uniprotRef.getDatabase().getName();
                    // we have a swissprot entry
                    if (swissprot.equalsIgnoreCase(databaseName) || swissprot_sv.equalsIgnoreCase(databaseName)) {
                        setOfSwissprotAccessions.add(uniprotRef.getAccession());
                    }
                    // we have a trembl entry
                    else {
                        setOfUniprotAccessions.add(uniprotRef.getAccession());
                    }
                }
            }
        }
    }

    /**
     * Query uniprot to get the swissprot entries with a specific cross reference or identifier and a specific organism. If the query is not successful in Swissprot,
     * Query uniprot without a filter on Swissprot.
     *
     * @param context : the context of the protein
     * @return a unique uniprot accession if possible, null otherwise
     * @throws ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // always clear the previous reports
        this.listOfReports.clear();

        String identifier = context.getIdentifier();
        Set<DatabaseType> databaseTypes = convertMINumberInUniprot(context.getDatabaseForIdentifier(), context.getDatabaseName());

        String taxId = null;

        // get the results of the query on swissprot
        QueryResult<UniProtEntry> iteratorSwissprot = getReviewedUniprotDatabaseCrossReferenceIterator(databaseTypes, identifier);
        // Create a new report
        MappingReport report = getReportsFactory().getMappingReport(ActionName.SEARCH_Swissprot_CrossReference);
        this.listOfReports.add(report);

        // if the organism is not null, we can add a filter on the organism
        if (context.getOrganism() != null) {
            taxId = context.getOrganism().getTaxId();
            iteratorSwissprot = addTaxIdToUniprotIterator(iteratorSwissprot, taxId);
        } else {
            report.addWarning("No organism was given for the protein with : identifier =  " + identifier + ". We will process the identification without looking at the organism.");
        }

        // if the query on Swissprot was not successful
        if (iteratorSwissprot == null || iteratorSwissprot.getNumberOfHits() == 0) {
            Status status = new Status(StatusLabel.FAILED, "There is no Swissprot entry matching the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
            report.setStatus(status);

            // new query, new report
            MappingReport report2 = getReportsFactory().getMappingReport(ActionName.SEARCH_Uniprot_CrossReference);
            this.listOfReports.add(report2);

            // get the results of the query on Trembl
            QueryResult<UniProtEntry> iteratorTrembl = getUnreviewedUniprotDatabaseCrossReferenceIterator(databaseTypes, identifier);

            if (taxId != null) {
                iteratorTrembl = addTaxIdToUniprotIterator(iteratorTrembl, taxId);
            }

            // If the query was not successful in Uniprot, we had a status FAILED to the report
            if (iteratorTrembl == null || iteratorTrembl.getNumberOfHits() == 0) {
                Status status2 = new Status(StatusLabel.FAILED, "There is no Uniprot entry matching the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                report2.setStatus(status2);
            }
            // query successful : process the results
            else {
                String ac = processIterator(iteratorTrembl, report2, context);
                if (ac != null) {
                    return ac;
                }
            }
        } else {
            // the results are not null on swissprot, we process them
            report.setIsASwissprotEntry(true);
            String ac = processIterator(iteratorSwissprot, report, context);
            if (ac != null) {
                return ac;
            }
        }

        // we couldn't find a result in uniprot
        if (report.getPossibleAccessions().isEmpty()) {

            // create a new report
            MappingReport reportUniparc = getReportsFactory().getMappingReport(ActionName.SEARCH_Uniparc_CrossReference);
            this.listOfReports.add(reportUniparc);

            // build query for uniparc
            // get the results of the query on uniparc
            QueryResult<UniParcEntry> iteratorUniparc = getUniparcDatabaseCrossReferenceIterator(identifier);

            if (taxId != null) {
                iteratorUniparc = addTaxIdToUniparcIterator(iteratorUniparc, taxId);
            }

            // We don't have any results in Uniparc
            if (iteratorUniparc == null || iteratorUniparc.getNumberOfHits() == 0) {
                Status statusUniparc = new Status(StatusLabel.FAILED, "There is no cross reference in Uniparc matching the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                reportUniparc.setStatus(statusUniparc);
            } else {
                // list of Trembl entries in the uniparc entry
                HashSet<String> setOfUniprotAccessions = new HashSet<String>();
                // list of Swissprot entries in the uniparc entry
                HashSet<String> setOfSwissprotAccessions = new HashSet<String>();

                for (UniParcEntry entry : getAllUniParcEntryQueryResults(iteratorUniparc)) {

                    // If the identifier can exactly match a uniparc accession
                    if (hasTheExactIdentifierInUniparc(entry, context.getIdentifier())) {
                        processUniparcResult(entry, taxId, setOfSwissprotAccessions, setOfUniprotAccessions);
                    }
                    // the identidier doesn't match any uniparc accessions or identifiers
                    else {
                        // get the database name in uniparc for what the identifier is matching
                        String databaseInUniparc = getTheDatabaseOfExactIdentifierInUniparcCrossReferences(entry, context.getIdentifier());

                        // if the exact identifier is in the cross references of the uniparc entry
                        if (databaseInUniparc != null) {
                            // We have a database name or MI for the identifier in the context
                            if (!databaseTypes.isEmpty()) {
                                for (DatabaseType name : databaseTypes) {
                                    // the database name is matching the one in the context
                                    if (name.toString().equalsIgnoreCase(databaseInUniparc)) {
                                        processUniparcResult(entry, taxId, setOfSwissprotAccessions, setOfUniprotAccessions);
                                    }
                                }
                            }
                        }
                    }
                }

                // No results
                if (setOfUniprotAccessions.isEmpty() && setOfSwissprotAccessions.isEmpty()) {
                    Status statusUniparc = new Status(StatusLabel.FAILED, "There is no uniprot entry we could find in Uniparc associated with the identifier " + identifier + (taxId != null ? " and the taxId " + taxId : ""));
                    reportUniparc.setStatus(statusUniparc);
                }
                // one swissprot entry
                else if (setOfSwissprotAccessions.size() == 1) {
                    String id = setOfSwissprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(true);

                    return id;
                }
                // several swissprot entries
                else if (setOfSwissprotAccessions.size() > 1) {
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfSwissprotAccessions.size() + " Swissprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfSwissprotAccessions);
                }
                // one trembl entry
                else if (setOfUniprotAccessions.size() == 1) {
                    String id = setOfUniprotAccessions.iterator().next();
                    Status statusUniparc = new Status(StatusLabel.COMPLETED, "The protein with the identifier " + context.getIdentifier() + " has successfully been identified as " + id + " in Uniparc.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.setIsASwissprotEntry(false);

                    return id;
                }
                // several trembl entries
                else {
                    Status statusUniparc = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the identifier " + context.getIdentifier() + " could match " + setOfUniprotAccessions.size() + " Uniprot entries.");
                    reportUniparc.setStatus(statusUniparc);
                    reportUniparc.getPossibleAccessions().addAll(setOfUniprotAccessions);
                }
            }
        }

        return null;
    }

    /**
     * Add a filter on the Taxid in the initial query
     *
     * @param initialquery : the initial query
     * @param organism     : the organism of the protein
     * @return the query as a String
     */
    protected QueryResult<UniParcEntry> addTaxIdToUniparcIterator(QueryResult<UniParcEntry> initialquery, String organism) {
        uniParcQueryService.start();
        try {
            QueryResult<UniParcEntry> queryResult = uniParcQueryService.getEntries(initialquery.getQuery().and(buildTaxIdQuery(organism)));
            uniParcQueryService.stop();
            return queryResult;
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        uniParcQueryService.stop();
        return null;
    }


    /**
     * Get all results of a UniParc query result
     *
     * @param queryResult : query result
     * @return Set<UniParcEntry>
     */
    private Set<UniParcEntry> getAllUniParcEntryQueryResults(QueryResult<UniParcEntry> queryResult) {
        Set<UniParcEntry> allQueryResults = new HashSet<UniParcEntry>();
        QueryResultPage<UniParcEntry> currentPage = queryResult.getCurrentPage();
        int count = 0;
        while (true) {
            for (UniParcEntry e : currentPage.getResults()) {
                allQueryResults.add(e);
                count++;
            }
            if (count < queryResult.getNumberOfHits()) {
                try {
                    currentPage.fetchNextPage();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return allQueryResults;
    }
}
