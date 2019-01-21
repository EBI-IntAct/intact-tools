package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.uniprot.rest.SearchDatabase;
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

/**
 * This class is doing a query using an identifier of the protein to identify. It will use the getUniprotBestGuess method of the Uniprot REST web service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class UniprotProteinAPISearchProcessWithAccession extends IdentificationActionImpl {

    /**
     * the Uniprot Protein API client
     */
    private UniprotProteinAPIClient uniprotRestClient;

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotProteinAPISearchProcessWithAccession.class );

    /**
     * Create a new UniprotProteinAPISearchProcessWithAccession
     */
    public UniprotProteinAPISearchProcessWithAccession(ReportsFactory factory){
        super(factory);
        this.uniprotRestClient = new UniprotProteinAPIClient();
    }

    /**
     * Call the getUniprotBestGuess method of the Uniprot Protein API web service.
     * @param context  : the context of the protein
     * @return an unique uniprot AC or null if it didn't find any matching uniprot entry
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // always clear the previous reports
        this.listOfReports.clear();

        String identifier = context.getIdentifier();
        String taxId = null;
        if (context.getOrganism() != null){
            taxId = context.getOrganism().getTaxId();
        }

        // create a new DefaultUniprotProteinAPIReport
        //TODO Review UniprotProteinAPIReport
        UniprotProteinAPIReport report = getReportsFactory().getUniprotProteinAPIReport(ActionName.UniprotProteinAPI_accession);
        this.listOfReports.add(report);

        if (taxId == null){
            report.addWarning("No organism was given for the identifier " + identifier + ". We will process the identification without looking at the organism and choose the entry with the longest sequence.");
        }

        try {
            // get the Uniprot best guess of Uniprot Protein API for this identifier and this taxId
            String [] idResults = this.uniprotRestClient.getUniprotBestGuessFor(identifier, taxId);
            String databaseName = null;
            String uniprotId = null;

            if (idResults != null && idResults.length == 2){
                // the database is always stored in the first String
                databaseName = idResults[0];
                // the uniprot AC is always stored in the second String
                uniprotId = idResults[1];
            }

            // If the uniprot AC is not null and the database is not null, we can filter the results
            if (uniprotId != null && databaseName != null){

                // we have a Swissprot entry
                if (databaseName.equals(SearchDatabase.SWISSPROT.toString()) || databaseName.equals(SearchDatabase.SWISSPROT_VARSPLIC.toString())){
                    Status status = new Status(StatusLabel.COMPLETED, "Uniprot Protein API successfully matched the identifier " + context.getIdentifier() + " to this Swissprot accession " + uniprotId);
                    report.setStatus(status);
                    report.setIsASwissprotEntry(true);
                    return uniprotId;
                }
                // we have a Trembl entry
                // TODO check if we still need this part -> || databaseName.equals(SearchDatabase.TREMBL_VARSPLIC.toString())
                else if (databaseName.equals(SearchDatabase.TREMBL.toString())){
                    Status status = new Status(StatusLabel.COMPLETED, "Uniprot Protein API successfully matched the identifier " + context.getIdentifier() + " to this Trembl accession " + uniprotId);
                    report.setStatus(status);
                    report.setIsASwissprotEntry(false);
                    return uniprotId;
                }
                else {
                    log.error("The database name " + databaseName + " is not expected. We are only expecting SWISSPROT, TREMBL, SWISSPROT_VARSPLIC, TREMBL_VARSPLIC");
                }
            }
            else {
                // the database can't be null if the uniprot id is not null
                if (databaseName == null && uniprotId != null){
                    log.error("The database name of the result " + uniprotId + " returned by Uniprot Protein API is null and should not be.");
                }

                // We didn't find anything, look if we have other cross references and add them to the list of cross references of the current report
                if (uniprotId == null){
                    Status status = new Status(StatusLabel.FAILED, "Uniprot Protein API couldn't match the identifier "+identifier+" to one Uniprot accession.");
                    report.setStatus(status);

                    List<Entry> upEntries = this.uniprotRestClient.getUPEntriesForAccession(identifier, taxId);

                    for (Entry e : upEntries){
                        for (DbReferenceType ref : e.getDbReference()){
                            report.addCrossReference(ref.getType(), ref.getId());
                        }
                    }
                }
            }
            // we can have a UniprotRESTClientException if the uniprotBestGuess method of the REST webservice is not finding a matching entry
        } catch (UniprotProteinAPIClientException e) {
            // If the report doesn't have any cross references, we can try to add them here and write the status of the last report
            if (report.getCrossReferences().isEmpty()){
                Status status = new Status(StatusLabel.FAILED, "Uniprot Protein API couldn't match the identifier "+identifier+" to any Uniprot accession.");
                report.setStatus(status);

                List<Entry> upEntries = null;
                try {
                    upEntries = this.uniprotRestClient.getUPEntriesForAccession(identifier, taxId);
                    for (Entry entry : upEntries){
                        for (DbReferenceType ref : entry.getDbReference()){
                            report.addCrossReference(ref.getType(), ref.getId());
                        }
                    }
                } catch (UniprotProteinAPIClientException e1) {
                    throw  new ActionProcessingException("Uniprot Protein API couldn't match the identifier " + identifier + " to any Uniprot accession. Check your identifier and/or organism.", e);
                }
                // Uniprot Protein API can throw this exception when NCBI not available or because of an internal error. Need to test later
                catch (Exception e1){
                    status = new Status(StatusLabel.TO_BE_REVIEWED, "Uniprot Protein API couldn't match the identifier "+identifier+" to any Uniprot accession because of an internal problem. Check this identifier later.");
                    report.setStatus(status);
                }
            }
            else {
                throw  new ActionProcessingException("Uniprot Protein API couldn't match the identifier " + identifier + " to any Uniprot accession. Check your identifier and/or organism.", e);
            }
        }
        // Uniprot Protein API can throw this exception when NCBI not available or because of an internal error. Need to test later
        catch (Exception e){
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Uniprot Protein API couldn't match the identifier "+identifier+" to any Uniprot accession because of an internal problem. Check this identifier later.");
            report.setStatus(status);
        }
        return null;
    }
}
