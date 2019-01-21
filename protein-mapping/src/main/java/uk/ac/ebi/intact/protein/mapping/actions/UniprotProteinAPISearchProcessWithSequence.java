package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.uniprot.rest.UniprotProteinAPIClient;
import psidev.psi.mi.jami.bridges.uniprot.rest.UniprotProteinAPIClientException;
import psidev.psi.mi.jami.bridges.uniprot.rest.response.model.DbReferenceType;
import psidev.psi.mi.jami.bridges.uniprot.rest.response.model.Entry;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.UniprotProteinAPIReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;
import java.util.Set;

/**
 * This class is doing a query using the sequence of the protein to identify. It will first query Uniprot REST web service
 * for swissprot cross references and if not, query Uniprot REST web service for Trembl cross references
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class UniprotProteinAPISearchProcessWithSequence extends IdentificationActionImpl {

    /**
     * the Uniprot Protein API client
     */
    private UniprotProteinAPIClient uniprotRestClient;

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotProteinAPISearchProcessWithSequence.class );

    /**
     * Create a new UniprotProteinAPISearchProcessWithSequence
     */
    public UniprotProteinAPISearchProcessWithSequence(ReportsFactory factory){
        super(factory);
        this.uniprotRestClient = new UniprotProteinAPIClient();
    }

    /**
     * Query with the sequence of the protein to identify. Query Uniprot Protein API on swissprot database first, if no results, query the Trembl database.
     * If several proteins are matching, they are added to the list of possible proteins in the DefaultUniprotProteinAPIReport
     * @param context  : the context of the protein
     * @return an unique uniprot AC if only one uniprot entry is matching the sequence, null otherwise
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // always clear the previous report
        this.listOfReports.clear();

        String sequence = context.getSequence();
        String taxId = null;
        if (context.getOrganism() != null){
            taxId = context.getOrganism().getTaxId();
        }

        // create a DefaultUniprotProteinAPIReport
        //TODO Review UniprotProteinAPIReport
        UniprotProteinAPIReport report = getReportsFactory().getUniprotProteinAPIReport(ActionName.UniprotProteinAPI_sequence_Swissprot);
        this.listOfReports.add(report);

        if (taxId == null){

            report.addWarning("No organism was given for the sequence " + sequence + ". We will process the identification without looking at the organism. We will keep only the swissprot results if there are both Swissprot and Trembl results.");
        }

        try {
            // Get the matching swissprot entries
            List<String> swissprotIds = this.uniprotRestClient.getSwissprotIdsForSequence(sequence, taxId);

            // We have an unique matching swissprot entry
            if (swissprotIds.size() == 1){
                if (swissprotIds.get(0) != null){
                    Status status = new Status(StatusLabel.COMPLETED, "Uniprot Protein API successfully returned an unique Swissprot accession " + swissprotIds.get(0));
                    report.setStatus(status);

                    report.setIsASwissprotEntry(true);
                    return swissprotIds.get(0);
                }
                else {
                    log.error("Uniprot Protein API returned an empty Swissprot accession for the sequence " + sequence);
                }
            }
            // we have several matching swissprot entries
            else if (swissprotIds.size() > 1){

                // We merge all the swissprot entries which are an isoform of the same protein
                Set<String> accessions = mergeIsoforms(swissprotIds);

                for (String ac : swissprotIds){
                    report.addPossibleAccession(ac);
                }

                // The different matching swissprot entries were in fact several isoforms of the same protein so we keep the canonical sequence
                if (accessions.size() == 1){
                    String ac = accessions.iterator().next();
                    report.setIsASwissprotEntry(true);
                    Status status = new Status(StatusLabel.COMPLETED, "We found a Unique Swissprot entry " + ac + " : the sequence matches several swissprot splice variant sequences of the same protein and we kept the canonical sequence.");

                    report.setStatus(status);
                    return ac;
                }
                // The different matching swissprot entries were different proteins so we can't choose
                else if (accessions.size() > 1){
                    Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Uniprot Protein API returned " + swissprotIds.size() + " Swissprot accessions which are matching the sequence.");
                    report.setStatus(status);
                }
                else {
                    log.error("Uniprot Protein API didn't return any valid results for the protein with the sequence "+ context.getSequence() +". Check the sequence.");
                }

                report.addWarning("Several SwissprotIds have been returned. We will not process the sequence mapping in Trembl.");
            }
            // We don't have any matching swissprot entries
            else {
                Status status = new Status(StatusLabel.FAILED, "Uniprot Protein API couldn't match the sequence to any Swissprot entries.");
                report.setStatus(status);

                // new Uniprot Protein API query so new report
                UniprotProteinAPIReport report2 = getReportsFactory().getUniprotProteinAPIReport(ActionName.UniprotProteinAPI_sequence_Trembl);
                this.listOfReports.add(report2);
                report2.getWarnings().addAll(report.getWarnings());

                // Get the matching trembl entries
                List<String> tremblIds = this.uniprotRestClient.getTremblIdsForSequence(sequence, taxId);

                // Only one matching Trembl entry
                if (tremblIds.size() == 1){
                    Status status2 = new Status(StatusLabel.COMPLETED, "Uniprot Protein API successfully returned an unique Trembl accession " + tremblIds.get(0));
                    report2.setStatus(status2);

                    report.setIsASwissprotEntry(false);
                    return tremblIds.get(0);
                }
                // Several trembl entries, we can't choose and we can't merge as we don't have trembl splice variants sequences
                else if (tremblIds.size() > 1){
                    
                    Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "Uniprot Protein API returned " + tremblIds.size() + " Trembl accessions which are matching the sequence.");
                    report2.setStatus(status2);

                    for (String ac : tremblIds){
                        report2.addPossibleAccession(ac);
                    }
                }
                // no matching Trembl entry
                else {
                    Status status2 = new Status(StatusLabel.FAILED, "Uniprot Protein API couldn't match any Uniprot entry to the sequence " + sequence);
                    report2.setStatus(status2);

                    Entry entry = this.uniprotRestClient.getUPEntriesForSequence(sequence, taxId);

                    if (entry != null){
                        for (DbReferenceType ref : entry.getDbReference()){
                            report.addCrossReference(ref.getType(), ref.getId());
                        }
                    }
                }
            }

        } catch (UniprotProteinAPIClientException e) {
            throw  new ActionProcessingException("Uniprot Protein API couldn't match the sequence " + sequence + " to any Uniprot accession. Check your identifier and/or organism.", e);
        }
        return null;
    }
}
