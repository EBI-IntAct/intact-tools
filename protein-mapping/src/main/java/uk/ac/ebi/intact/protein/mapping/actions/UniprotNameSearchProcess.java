package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Section;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.GeneNameSynonym;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.ORFName;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.OrderedLocusName;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.QueryResultPage;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.query.Query;

/**
 * This class is querying Uniprot for a gene name and/or protein name which is matching the name of the protein to identify
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class UniprotNameSearchProcess extends ActionNeedingUniprotService {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(UniprotNameSearchProcess.class);

    public UniprotNameSearchProcess(ReportsFactory factory) {
        super(factory);
    }

    /**
     * Create a query to get the Uniprot entries with this gene name
     *
     * @param gene_name : the gene name of the protein to identify
     * @return Query
     */
    private Query buildGeneNameQuery(String gene_name) {
        return UniProtQueryBuilder.gene(gene_name);
    }

    /**
     * Create a query to get the Uniprot entries with this protein name
     *
     * @param protein_name : the protein name of the protein to identify
     * @return Query
     */
    private Query buildProteinNameQuery(String protein_name) {
        return UniProtQueryBuilder.proteinName(protein_name);
    }

    /**
     * Create a query to get the uniprot entries with this gene name AND this protein name
     *
     * @param gene_name    : the gene name of the protein
     * @param protein_name : the protein name of the protein
     * @return Query
     */
    private Query buildGenenameAndProteinNameQuery(String gene_name, String protein_name) {
        return buildGeneNameQuery(gene_name).and(buildProteinNameQuery(protein_name));
    }

    /**
     * Create a query to get the uniprot entries with this gene name OR this protein name
     *
     * @param gene_name    : the gene name of the protein
     * @param protein_name : the protein name of the protein
     * @return Query
     */
    private Query buildGenenameOrProteinNameQuery(String gene_name, String protein_name) {
        return buildGeneNameQuery(gene_name).or(buildProteinNameQuery(protein_name));
    }

    /**
     * Check that the matching uniprot entry has this exact gene name
     *
     * @param protein  : the matching Uniprot entry
     * @param geneName : the gene name of the protein to identify
     * @return true if the matching protein has the excat gene name
     */
    private boolean hasTheExactGeneName(UniProtEntry protein, String geneName) {
        // No genes
        if (protein.getGenes().isEmpty()) {
            return false;
        } else {
            for (Gene gene : protein.getGenes()) {
                // Check the gene name
                if (geneName.equalsIgnoreCase(gene.getGeneName().getValue())) {
                    return true;
                }
                // Check the gene synonyms
                if (!gene.getGeneNameSynonyms().isEmpty()) {
                    for (GeneNameSynonym synonym : gene.getGeneNameSynonyms()) {
                        if (geneName.equalsIgnoreCase(synonym.getValue())) {
                            return true;
                        }
                    }
                }
                // Check the ordered locus names
                if (!gene.getOrderedLocusNames().isEmpty()) {
                    for (OrderedLocusName orderedLocusName : gene.getOrderedLocusNames()) {
                        if (geneName.equalsIgnoreCase(orderedLocusName.getValue())) {
                            return true;
                        }
                    }
                }
                // Check the ORF names
                if (!gene.getORFNames().isEmpty()) {
                    for (ORFName orfName : gene.getORFNames()) {
                        if (geneName.equalsIgnoreCase(orfName.getValue())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * process the results of the query on uniprot
     *
     * @param iterator : the iterator of the Uniprot results
     * @param report   : the current report
     * @param context  : the context of the protein to identify
     * @return the unique uniprot id if an unique unprot entry, null otherwise
     */
    private String processQuery(QueryResult<UniProtEntry> iterator, MappingReport report, IdentificationContext context) {
        // we have only one entry
        if (iterator.getNumberOfHits() == 1) {
            String id = iterator.next().getPrimaryUniProtAccession().getValue();
            Status status = new Status(StatusLabel.COMPLETED, "The protein " + (context.getGene_name() != null ? "with the gene name " + context.getGene_name() : (context.getProtein_name() != null ? "with the protein name " + context.getProtein_name() : "")) + " has successfully been identified as " + id);
            report.setStatus(status);

            return id;
        }
        // we have several matching entries
        else if (iterator.getNumberOfHits() > 1) {
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein " + (context.getGene_name() != null ? "with the gene name " + context.getGene_name() : (context.getProtein_name() != null ? "with the protein name " + context.getProtein_name() : "")) + " could match " + iterator.getNumberOfHits() + " Uniprot entries.");
            report.setStatus(status);
            report.setIsASwissprotEntry(false);
            QueryResultPage<UniProtEntry> currentPage = iterator.getCurrentPage();
            int count = 0;
            while (true) {
                for (UniProtEntry e : currentPage.getResults()) {
                    report.addPossibleAccession(e.getPrimaryUniProtAccession().getValue());
                    count++;
                }
                if (count < iterator.getNumberOfHits()) {
                    try {
                        currentPage.fetchNextPage();
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
        return null;
    }

    /**
     * process the results of the query on the gene name
     *
     * @param iterator : the iterator of the Uniprot results
     * @param report   : the current report
     * @param context  : the context of the protein to identify
     * @return the unique uniprot id if an unique unprot entry, null otherwise
     */
    private String processGeneQuery(QueryResult<UniProtEntry> iterator, MappingReport report, IdentificationContext context) {
        // We have only one matching protein
        if (iterator.getNumberOfHits() == 1) {
            String id = iterator.next().getPrimaryUniProtAccession().getValue();
            Status status = new Status(StatusLabel.COMPLETED, "The protein with the gene name " + context.getGene_name() + " has successfully been identified as " + id);
            report.setStatus(status);

            return id;
        }
        // we have several matching uniprot entries
        else if (iterator.getNumberOfHits() > 1) {

            // check if there are some entries with the exact gene name
            QueryResultPage<UniProtEntry> currentPage = iterator.getCurrentPage();
            int count = 0;
            while (true) {
                for (UniProtEntry e : currentPage.getResults()) {
                    if (hasTheExactGeneName(e, context.getGene_name())) {
                        report.addPossibleAccession(e.getPrimaryUniProtAccession().getValue());
                    }
                    count++;
                }
                if (count < iterator.getNumberOfHits()) {
                    try {
                        currentPage.fetchNextPage();
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            // The entries don't have the exact gene name
            if (report.getPossibleAccessions().isEmpty()) {
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the gene name " + context.getGene_name() + " could match " + iterator.getNumberOfHits() + " Uniprot entries.");
                report.setStatus(status);
                
                currentPage = iterator.getCurrentPage();
                count = 0;
                while (true){
                    for (UniProtEntry e : currentPage.getResults()){
                        report.addPossibleAccession(e.getPrimaryUniProtAccession().getValue());
                        count++;
                    }
                    if (count < iterator.getNumberOfHits()) {
                        try {
                            currentPage.fetchNextPage();
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
            // One of the uniprot entries has the exact gene name, we keep it
            else if (report.getPossibleAccessions().size() == 1) {
                String ac = report.getPossibleAccessions().iterator().next();
                Status status = new Status(StatusLabel.COMPLETED, "The protein with the exact gene name " + context.getGene_name() + " has successfully been identified as " + ac);
                report.setStatus(status);

                return ac;
            }
            // Several uniprot entries have the exact gene name, we keep them
            else {
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein with the gene name " + context.getGene_name() + " could match " + iterator.getNumberOfHits() + " Uniprot entries.");
                report.setStatus(status);
            }
        }
        return null;
    }

    /**
     * Get the results of the query on Swissprot with this gene name, this protein name, this name and/or this organism name
     *
     * @param geneName    : the gene name of the protein
     * @param proteinName : the protein name of the protein
     * @param globalName  : the name of the protein
     * @param organism    : the organism name of the protein
     * @return the iterator of the results, null if no gene name, no protein name and no global name
     */
    public QueryResult<UniProtEntry> querySwissprotWithGeneNameOrProteinName(String geneName, String proteinName, String globalName, String organism) {
        Query query = null;

        // protein name and gene name not null : we query specifically uniprot with this gene name and this protein name
        if (proteinName != null && geneName != null) {
            query = addFilterOnSwissprot(buildGenenameOrProteinNameQuery(geneName, proteinName));

            if (organism != null) {
                query = addTaxIdToQuery(query, organism);
            }
            uniProtQueryService.start();
            QueryResult<UniProtEntry> iterator = null;
            try {
                iterator = uniProtQueryService.getEntries(query);
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
            uniProtQueryService.stop();
            return iterator;
        }
        // general name not null : we query swissprot with this name
        else if (globalName != null) {
            query = buildGenenameOrProteinNameQuery(globalName, globalName);
            query = query.and(UniProtQueryBuilder.swissprot());
            uniProtQueryService.start();
            if (organism != null) {
                query = addTaxIdToQuery(query, organism);
            }
            QueryResult<UniProtEntry> iterator = null;
            try {
                iterator = uniProtQueryService.getEntries(query);
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
            uniProtQueryService.stop();
            if (iterator != null && iterator.getNumberOfHits() != 0) {
                return getExactUniprotEntryMatchResult(iterator, globalName);
            }
        }
        return null;
    }

    /**
     * Get the results of the query on Trembl with this gene name, this protein name, this name and/or this organism name
     *
     * @param geneName    : the gene name of the protein
     * @param proteinName : the protein name of the protein
     * @param globalName  : the name of the protein
     * @param organism    : the organism name of the protein
     * @return the iterator of the results, null if no gene name, no protein name and no global name
     */
    public QueryResult<UniProtEntry> queryUniprotWithGeneNameOrProteinName(String geneName, String proteinName, String globalName, String organism) {
        Query query = null;

        // protein name and gene name not null : we query specifically uniprot with this gene name and this protein name
        if (proteinName != null && geneName != null) {
            query = buildGenenameOrProteinNameQuery(geneName, proteinName);

            if (organism != null) {
                query = addTaxIdToQuery(query, organism);
            }

            QueryResult<UniProtEntry> iterator = null;
            uniProtQueryService.start();
            try {
                iterator = uniProtQueryService.getEntries(query);
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
            uniProtQueryService.stop();
            return iterator;
        }
        // general name not null : we query uniprot with this name
        else if (globalName != null) {
            query = buildGenenameOrProteinNameQuery(globalName, globalName);

            if (organism != null) {
                query = addTaxIdToQuery(query, organism);
            }

            QueryResult<UniProtEntry> iterator = null;
            uniProtQueryService.start();
            try {
                iterator = uniProtQueryService.getEntries(query);
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
            uniProtQueryService.stop();
            return iterator;
        }
        uniProtQueryService.stop();
        return null;
    }

    /**
     * get the results on Swissprot with this gene name and/or this protein name and this organism
     *
     * @param geneName    : the gene name of the protein
     * @param proteinName : the protein name of the protein
     * @param organism    : the organism name of the protein
     * @return the iterator of the results, null if no gene name and no protein name
     */
    public QueryResult<UniProtEntry> querySwissprotWith(String geneName, String proteinName, String organism) {
        Query query = null;

        // gene name and protein name not null : we query specifically swissprot for this gene name AND this protein name
        if (geneName != null && proteinName != null) {
            query = addFilterOnSwissprot(buildGenenameAndProteinNameQuery(geneName, proteinName));

        }
        // if the gene name is not null, we query specifically swissprot for this gene name
        else if (geneName != null) {
            query = addFilterOnSwissprot(buildGeneNameQuery(geneName));
        }
        // if the protein name is not null, we query specifically swissprot for this protein name
        else if (proteinName != null) {
            query = addFilterOnSwissprot(buildProteinNameQuery(proteinName));
        } else {
            log.error("Either the gene name or the protein name should be not null.");
        }

        // add a possible filter on the organism
        if (query != null) {
            if (organism != null) {
                query = addTaxIdToQuery(query, organism);
            }
            QueryResult<UniProtEntry> iterator = null;
            try {
                uniProtQueryService.start();
                iterator = uniProtQueryService.getEntries(query);
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
            uniProtQueryService.stop();
            return iterator;
        }
        uniProtQueryService.stop();
        return null;
    }

    /**
     * get the results on uniprot with this gene name and/or this protein name and this organism
     *
     * @param geneName    : the gene name of the protein
     * @param proteinName : the protein name of the protein
     * @param organism    : the organism name of the protein
     * @return the iterator of the results, null if no gene name and no protein name
     */
    public QueryResult<UniProtEntry> queryUniprotWith(String geneName, String proteinName, String organism) {
        Query query = null;

        // gene name and protein name not null : we query specifically uniprot for this gene name AND this protein name
        if (geneName != null && proteinName != null) {
            query = buildGenenameAndProteinNameQuery(geneName, proteinName);
        }
        // if the gene name is not null, we query specifically uniprot for this gene name
        else if (geneName != null) {
            query = buildGeneNameQuery(geneName);
        }
        // if the protein name is not null, we query specifically uniprot for this protein name
        else if (proteinName != null) {
            query = buildProteinNameQuery(proteinName);
        } else {
            log.error("Either the gene name or the protein name should be not null.");
        }

        // add a possible filter on the organism
        if (query != null) {
            if (organism != null) {
                query = addTaxIdToQuery(query, organism);
            }
            QueryResult<UniProtEntry> iterator = null;
            try {
                uniProtQueryService.start();
                iterator = uniProtQueryService.getEntries(query);
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
            uniProtQueryService.stop();
            return iterator;
        }
        uniProtQueryService.stop();
        return null;
    }

    /**
     * process the name search on swissprot first and then on Trembl.
     *
     * @param geneName     : the gene name of the protein
     * @param protein_name : the protein name of the protein
     * @param globalName   : the name of the protein
     * @param organism     : the organism name of the protein
     * @param report       : the current report
     * @param context      : the current context
     * @return an unique uniprot AC if possible, null otherwise
     */
    private String processNameSearch(String geneName, String protein_name, String organism, String globalName, MappingReport report, IdentificationContext context) {
        // If the gene name or the protein name is not null, we can do a specific search on Uniprot
        if (geneName != null || protein_name != null) {

            // get the results on swissprot
            QueryResult<UniProtEntry> iterator = querySwissprotWith(geneName, protein_name, organism);

            // if we don't have any results, we look into trembl
            if (iterator == null || iterator.getNumberOfHits() == 0) {
                report.setIsASwissprotEntry(false);
                Status status = new Status(StatusLabel.FAILED, "We couldn't find any Swissprot entry which matches : name = " + globalName + "; TaxId = " + organism + ". We will look in Trembl.");
                report.setStatus(status);

                // get the results on Trembl
                iterator = queryUniprotWith(geneName, protein_name, organism);
                MappingReport report2 = getReportsFactory().getMappingReport(ActionName.SEARCH_uniprot_name);
                this.listOfReports.add(report2);

                // if we don't have any result, the search fails
                if (iterator == null || iterator.getNumberOfHits() == 0) {
                    Status status2 = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches : gene name = " + geneName + "; protein name = " + protein_name + "; TaxId = " + organism);
                    report2.setStatus(status2);
                }
                // we process the results
                else {
                    return processQuery(iterator, report, context);
                }
            }
            // we have several results and the gene name is not null : we can try to get the entries with the exact gene name
            else if (iterator.getNumberOfHits() > 1 && geneName != null) {
                return processGeneQuery(iterator, report, context);
            }
            // we have several results and the gene name is null : we process the results
            else {
                return processQuery(iterator, report, context);
            }
        }
        // no gene name and no protein name but a general name is given
        else if (globalName != null) {
            // get the results on swissprot
            QueryResult<UniProtEntry> iterator = querySwissprotWithGeneNameOrProteinName(null, null, globalName, organism);

            // if we don't have any results, we look into trembl
            if (iterator == null || iterator.getNumberOfHits() == 0) {
                Status status = new Status(StatusLabel.FAILED, "We couldn't find any Swissprot entry which matches : name = " + globalName + "; TaxId = " + organism + ". We will look in Trembl.");
                report.setStatus(status);

                // get the results on uniprot
                iterator = queryUniprotWithGeneNameOrProteinName(null, null, globalName, organism);

                MappingReport report2 = getReportsFactory().getMappingReport(ActionName.SEARCH_uniprot_name);
                this.listOfReports.add(report2);

                // if we don't have any result, the search fails
                if (iterator == null || iterator.getNumberOfHits() == 0) {
                    Status status2 = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches : name = " + globalName + "; TaxId = " + organism);
                    report2.setStatus(status2);
                }
                // we process the results
                else {
                    return processQuery(iterator, report, context);
                }
            }
            // we process the results
            else {
                return processQuery(iterator, report, context);
            }
        } else {
            log.error("We don't have any name to query for.");
        }
        return null;
    }

    /**
     * We store the results of the query in the report
     *
     * @param report : the current report
     * @param query  : the query
     * @param searchTerm : the exact term, which is search for               
     */
    private void processGlobalQuery(MappingReport report, Query query, String searchTerm) {
        QueryResult<UniProtEntry> iterator = null;
        uniProtQueryService.start();
        try {
            iterator = uniProtQueryService.getEntries(query);
        } catch (ServiceException e) {
            uniProtQueryService.stop();
            e.printStackTrace();
        }
        if (iterator != null && iterator.getNumberOfHits() != 0) {
            iterator = getExactUniprotEntryMatchResult(iterator, searchTerm);
            if (iterator != null && iterator.getNumberOfHits() != 0) {
                QueryResultPage<UniProtEntry> currentPage = iterator.getCurrentPage();
                int count = 0;
                while (true) {
                    for (UniProtEntry e : currentPage.getResults()) {
                        report.addPossibleAccession(e.getPrimaryUniProtAccession().getValue());
                        count++;
                    }
                    if (count < iterator.getNumberOfHits()) {
                        try {
                            currentPage.fetchNextPage();
                        } catch (ServiceException e) {
                            uniProtQueryService.stop();
                            e.printStackTrace();
                        }
                    } else {
                        uniProtQueryService.stop();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Query uniprot for a gene name and/or protein name first with possible filter on the organism, widen the search if no results but always keep the filter on the organism
     *
     * @param context : the context of the protein
     * @return an unique uniprot id or null if no result or several possible proteins
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // always clear the previous reports
        this.listOfReports.clear();

        // Create a new report
        MappingReport report = getReportsFactory().getMappingReport(ActionName.SEARCH_uniprot_name);
        this.listOfReports.add(report);

        String geneName = context.getGene_name();
        String protein_name = context.getProtein_name();
        String organism = null;
        if (context.getOrganism() != null) {
            organism = context.getOrganism().getTaxId();
        } else {
            report.addWarning("No organism was given for the protein with : name =  " + context.getGlobalName() != null ? context.getGlobalName() : (context.getGene_name() != null ? context.getGene_name() : (context.getProtein_name() != null ? context.getProtein_name() : "")) + ". We will process the identification without looking at the organism.");
        }

        String globalName = context.getGlobalName();

        report.setIsASwissprotEntry(true);

        // process a name search using gene name, protein name and/or glocal name
        String accession = processNameSearch(geneName, protein_name, organism, globalName, report, context);

        // the specific search is successful
        if (accession != null) {
            return accession;
        }
        // the specific search is unsuccessful  : no results
        else if (report.getPossibleAccessions().isEmpty()) {

            // Create a new report
            MappingReport report2 = getReportsFactory().getBlastReport(ActionName.wide_SEARCH_uniprot);
            this.listOfReports.add(report2);

            // get non specific results with gene name or protein name or global name
            if (geneName != null) {
                Query query = UniProtQueryBuilder.gene(geneName).and(UniProtQueryBuilder.taxonID(Integer.parseInt(organism)));
                processGlobalQuery(report2, query, geneName);
            }
            if (protein_name != null) {
                Query query2 = UniProtQueryBuilder.proteinName(protein_name).and(UniProtQueryBuilder.taxonID(Integer.parseInt(organism)));
                processGlobalQuery(report2, query2, protein_name);
            }
            if (globalName != null) {
                Query query3 = UniProtQueryBuilder.gene(globalName).or(UniProtQueryBuilder.proteinName(globalName)).and(UniProtQueryBuilder.taxonID(Integer.parseInt(organism)));
                processGlobalQuery(report2, query3, globalName);
            }

            // no results
            if (report2.getPossibleAccessions().isEmpty()) {
                Status status = new Status(StatusLabel.FAILED, "We couldn't find any Uniprot entry which matches the name even after we had widened the search.");
                report2.setStatus(status);
            }
            // we have some results but there are not specific, we need a curator to decide
            else {
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "We found " + report2.getPossibleAccessions().size() + " Uniprot entry(ies) which matche(s) the name after we had widened the search.");
                report2.setStatus(status);
            }
        }
        return null;
    }

    /**
     * Based on a search result, we do a search over the whole protein description using the searchterm. This algorithm
     * has been provided by Jie Lou (UniProt), but as Lambada function. We needed to convert it back to be compatible*
     *
     * @param entry      : search result
     * @param searchTerm : term we are looking for.
     * @return boolean. If true we have found something
     */
    private static boolean findExactUniProtEntry(UniProtEntry entry, String searchTerm) {
        ProteinDescription proteinDescription = entry.getProteinDescription();
        for (Name name : proteinDescription.getSection().getNames()) {
            for (Field field : name.getFields()) {
                if (field.getValue().equalsIgnoreCase(searchTerm)) {
                    return true;
                }
            }
        }
        for (Section section : proteinDescription.getContains()) {
            for (Name names : section.getNames()) {
                for (Field field : names.getFields()) {
                    if (field.getValue().equalsIgnoreCase(searchTerm)) {
                        return true;
                    }
                }

            }
        }
        for (Section section : proteinDescription.getIncludes()) {
            for (Name names : section.getNames()) {
                for (Field field : names.getFields()) {
                    if (field.getValue().equalsIgnoreCase(searchTerm)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    /**
     * Creates a query to retrieve 1-n UniPort entries based on their AC
     *
     * @param iterator    : the initial query
     * @param searchTerm: the ac which needs to be add to the initialQuery
     * @return Query
     */
    private QueryResult<UniProtEntry> getExactUniprotEntryMatchResult(QueryResult<UniProtEntry> iterator, String searchTerm) {
        Query query = null;
        QueryResultPage<UniProtEntry> currentPage = iterator.getCurrentPage();
        int count = 0;
        while (true) {
            for (UniProtEntry e : currentPage.getResults()) {
                if (findExactUniProtEntry(e, searchTerm)) {
                    query = buildOrPrimaryUniProtAccessionQuery(query, e.getPrimaryUniProtAccession().getValue());
                }
                count++;
            }
            if (count < iterator.getNumberOfHits()) {
                try {
                    currentPage.fetchNextPage();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        if (query != null) {
            QueryResult<UniProtEntry> iterator2 = null;
            uniProtQueryService.start();
            try {
                iterator2 = uniProtQueryService.getEntries(query);
            } catch (ServiceException e) {
                uniProtQueryService.stop();
                e.printStackTrace();
            }
            uniProtQueryService.stop();
            return iterator2;
        }
        return null;
    }

    /**
     * Creates a query to retrieve 1..n UniPort entries based on their AC
     *
     * @param initialQuery : the initial query
     * @param ac           : the ac which needs to be add to the initialQuery
     * @return Query
     */
    private Query buildOrPrimaryUniProtAccessionQuery(Query initialQuery, String ac) {
        if (initialQuery == null) {
            return UniProtQueryBuilder.accession(ac);
        }
        return initialQuery.or(UniProtQueryBuilder.accession(ac));
    }
}
