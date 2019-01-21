package uk.ac.ebi.intact.protein.mapping.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.protein.mapping.actions.CrossReferenceSearchProcess;
import uk.ac.ebi.intact.protein.mapping.actions.IdentificationAction;
import uk.ac.ebi.intact.protein.mapping.actions.UniprotProteinAPISearchProcessWithAccession;
import uk.ac.ebi.intact.protein.mapping.actions.SwissprotRemappingProcess;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.BlastContext;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.UniprotService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This strategy aims at identifying a protein using its identifier and organism. It can be also a complex IdentificationAction
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class StrategyWithIdentifier extends IdentificationStrategyImpl implements IdentificationAction {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithIdentifier.class );

    /**
     * The list of reports of this action
     */
    private List<MappingReport> listOfReports = new ArrayList<MappingReport> ();

    /**
     * the list of special organisms entirely sequenced
     */
    private static ArrayList<String> organismWithSpecialCase = new ArrayList<String>();

    /**
     * List of database MI numbers that Uniprot Protein API can manage
     */
    public static Set<String> listOfDatabaseNamesManagedByUniprotProteinAPI = new HashSet<String>();

    /**
     * List of database MI numbers that Uniprot Protein API can manage
     */
    public static Set<String> listOfMIDatabasesManagedByUniprotProteinAPI = new HashSet<String>();

    /**
     * Create a new strategy with identifier
     */
    public StrategyWithIdentifier(){
        super();
        if (listOfMIDatabasesManagedByUniprotProteinAPI.isEmpty()){
            initialiseListOfMIDatabasesManagedByUniprotProteinAPI();
            initialiseListOfDatabaseNamesManagedByUniprotProteinAPI();
        }
    }

    public StrategyWithIdentifier(UniprotService uniprotService) {
        super(uniprotService);
        if (listOfMIDatabasesManagedByUniprotProteinAPI.isEmpty()){
            initialiseListOfMIDatabasesManagedByUniprotProteinAPI();
            initialiseListOfDatabaseNamesManagedByUniprotProteinAPI();
        }
    }

    private void initialiseListOfMIDatabasesManagedByUniprotProteinAPI(){
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.UNIPROT_MI_REF);
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.FLYBASE_MI_REF);
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.DDBG_MI_REF);
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.REFSEQ_MI_REF);
        // genbank
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0860");
        // genbank protein
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0851");
        // genbank nucl
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0852");
        // wormbase
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0487");
        // ipi
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0675");
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.ENSEMBL_MI_REF);
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.WWPDB_MI_REF);
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.RCSB_PDB_MI_REF);
        // het
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:2017");
        // pdbe
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0472");
        // emdb
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0936");
        // pdbj
        listOfMIDatabasesManagedByUniprotProteinAPI.add("MI:0806");
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.SGD_MI_REF);
        listOfMIDatabasesManagedByUniprotProteinAPI.add(CvDatabase.UNIPARC_MI_REF);
    }

    private void initialiseListOfDatabaseNamesManagedByUniprotProteinAPI(){
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.UNIPROT);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("SwissProt");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("TrEMBL");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.FLYBASE);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.DDBG);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("EMBL");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.REFSEQ);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("genbank indentifier");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("genbank_protein_gi");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("genbank_nucl_gi");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("JPO");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("PIR");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("TAIR");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("UniMES");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("USPTO");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("wormbase");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("SEGUID");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("ipi");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.ENSEMBL);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("EPO");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("H Inv");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("PDB");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.WWPDB);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.RCSB_PDB);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("het");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("pdbe");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("eMDB");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("pdbj");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("PRF");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.SGD);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("TROME");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add(CvDatabase.UNIPARC);
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("VEGA");
        listOfDatabaseNamesManagedByUniprotProteinAPI.add("KIPO");
    }

    /**
     * Initialise the set of actions for this strategy
     */
    protected void initialiseSetOfActions(){

        // first action = UniprotProteinAPISearchProcessWithAccession
        UniprotProteinAPISearchProcessWithAccession firstAction = new UniprotProteinAPISearchProcessWithAccession(getReportsFactory());
        this.listOfActions.add(firstAction);

        // second action = CrossReferenceSearchProcess (optional)
        CrossReferenceSearchProcess secondAction = new CrossReferenceSearchProcess(getReportsFactory(), uniprotService);
        this.listOfActions.add(secondAction);

        // third action = SwissprotRemappingProcess
        SwissprotRemappingProcess thirdAction = new SwissprotRemappingProcess(getReportsFactory());
        this.listOfActions.add(thirdAction);
    }

    /**
     * initialises the list of special organisms
     */
    public static final void initialiseOrganismWithSpecialCase(){
        organismWithSpecialCase.add("9606");
        organismWithSpecialCase.add("4932");
        organismWithSpecialCase.add("4896");
        organismWithSpecialCase.add("562");
        organismWithSpecialCase.add("1423");
    }

    /**
     *
     * @param taxId : the taxId to check
     * @return  true if it is a special organism entirely sequenced
     */
    private boolean isASpecialOrganism(String taxId){

        if (organismWithSpecialCase.isEmpty()){
            initialiseOrganismWithSpecialCase();
        }

        if (organismWithSpecialCase.contains(taxId)){
            return true;
        }
        return false;
    }

    private boolean isADatabaseManagedByUniprotProteinAPI(String database, String databaseName){

        if (database != null){
            if (listOfMIDatabasesManagedByUniprotProteinAPI.contains(database)){
                return true;
            }
            else {
                for (String name : listOfDatabaseNamesManagedByUniprotProteinAPI){
                    if (name.equalsIgnoreCase(database)){
                        return true;
                    }
                }
            }
        }
        else if (databaseName != null){
            for (String name : listOfDatabaseNamesManagedByUniprotProteinAPI){
                if (name.equalsIgnoreCase(database)){
                    return true;
                }
            }
        }
        return false;
    }

    private void runCrossReferenceProcess(IdentificationContext context, IdentificationResults result) throws ActionProcessingException {
        String uniprotResult = this.listOfActions.get(1).runAction(context);
        // get the reports of the second action
        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

        MappingReport lastReport = result.getLastAction();

        processIsoforms(uniprotResult, result);
    }

    private String runCrossReferenceProcess(IdentificationContext context) throws ActionProcessingException {
        String uniprot = this.listOfActions.get(1).runAction(context);
        // get the reports of the second action
        this.listOfReports.addAll(this.listOfActions.get(1).getListOfActionReports());
        // process the isoforms and set the uniprot id of the result
        uniprot = processIsoforms(uniprot);

        return uniprot;
    }

    /**
     * This strategy is using Uniprot Protein API and/or uniprot cross reference search to map the identifier to an unique uniprot AC. If an unique Trembl is found,
     * the strategy will use the SwissprotRemappingProcess to remap the trembl entry to a Swissprot entry.
     * @param context : the context of the protein to identify
     * @return the results
     * @throws uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException
     */
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        // new result
        IdentificationResults result = getResultsFactory().getIdentificationResults();

        // the strategy is based on the identifier
        if (context.getIdentifier() == null){
            throw new StrategyException("The identifier of the protein must be not null.");
        }
        else{

            try {
                String uniprot = null;

                if (context.getDatabaseForIdentifier() == null && context.getDatabaseName() == null){
                    log.warn("The identifier " + context.getIdentifier() + " is not associated to a database name or MI, so we will not use Uniprot Protein API" +
                            " to map this identifier to an uniprot entry.");
                }

                if (isADatabaseManagedByUniprotProteinAPI(context.getDatabaseForIdentifier(), context.getDatabaseName())){
                    // result of Uniprot Protein API
                    uniprot = this.listOfActions.get(0).runAction(context);
                    // get the reports of the first action
                    result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());

                    if (uniprot == null && result.getLastAction().getPossibleAccessions().isEmpty()){
                        String taxId = null;
                        if (context.getOrganism() != null){
                            taxId = context.getOrganism().getTaxId();
                        }

                        if (taxId != null){
                            if (!taxId.startsWith("-")){
                                runCrossReferenceProcess(context, result);
                            }
                        }
                        else {
                            runCrossReferenceProcess(context, result);
                        }
                    }

                    // process the isoforms and set the uniprot id of the result
                    processIsoforms(uniprot, result);
                }
                else {
                    runCrossReferenceProcess(context, result);
                }

                // Uniprot Protein API and uniprot could map the identifier to a Swissprot accession
                if (result.getFinalUniprotId() != null){
                    MappingReport report = result.getLastAction();

                    // Uniprot Protein API or uniprot could map to a Trembl entry
                    if (!report.isASwissprotEntry()){

                        // get the uniprot protein
                        UniprotProtein tremblEntry = getUniprotProteinFor(result.getFinalUniprotId());
                        String sequence = tremblEntry.getSequence();

                        // create a blast context for the swissprotRemapping process
                        BlastContext blastContext = new BlastContext(context);
                        blastContext.setSequence(sequence);

                        // the Trembl entry should not be null
                        if (tremblEntry != null){

                            // we extract its ensembl gene accession
                            String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                            blastContext.setEnsemblGene(ensemblGene);
                        }
                        else {
                            throw new StrategyException("We couldn't find any Uniprot entries which match this accession number " + result.getFinalUniprotId());
                        }

                        // run the swissprotRemappingProcess
                        uniprot = this.listOfActions.get(2).runAction(blastContext);
                        // add the reports of the second action
                        result.getListOfActions().addAll(this.listOfActions.get(2).getListOfActionReports());
                        // process the isoforms and set the uniprot id of the result
                        processIsoforms(uniprot, result);

                        List<BlastReport> listOfSwissprotRemappingReports = getSwissprotRemappingReports(result.getListOfActions());

                        for (BlastReport<BlastResults> sr : listOfSwissprotRemappingReports){
                            for (BlastResults r : sr.getBlastMatchingProteins()){
                                r.setTremblAccession(tremblEntry.getPrimaryAc());
                            }
                        }
                    }
                }

            } catch (ActionProcessingException e) {

                throw  new StrategyException("An error occured while trying to identify the protein using the identifier " + context.getIdentifier(), e);

            }
            return result;
        }
    }

    /**
     * This action is using Uniprot Protein API and/or uniprot cross reference search to map the identifier to an unique uniprot AC. If an unique Trembl is found,
     * the strategy will use the SwissprotRemappingProcess to remap the trembl entry to a Swissprot entry.
     * @param context  : the context of the protein
     * @return an unique uniprot Accession if possible, null otherwise
     * @throws ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // Always clear the previous reports
        this.listOfReports.clear();
        String uniprot = null;

        if (context.getDatabaseForIdentifier() == null && context.getDatabaseName() == null){
            log.warn("The identifier " + context.getIdentifier() + " is not associated to a database name or MI, so we will not use Uniprot Protein API" +
                    " to map this identifier to an uniprot entry.");
        }

        if (isADatabaseManagedByUniprotProteinAPI(context.getDatabaseForIdentifier(), context.getDatabaseName())){
            // result of Uniprot Protein API
            uniprot = this.listOfActions.get(0).runAction(context);
            // get the reports of the first action
            this.listOfReports.addAll(this.listOfActions.get(0).getListOfActionReports());

            MappingReport lastReport =  this.listOfReports.get(this.listOfReports.size() - 1);

            if (uniprot == null && lastReport.getPossibleAccessions().isEmpty()){
                String taxId = null;
                if (context.getOrganism() != null){
                    taxId = context.getOrganism().getTaxId();
                }

                if (taxId != null){
                    if (!taxId.startsWith("-")){
                        uniprot = runCrossReferenceProcess(context);
                    }
                }
                else {
                    uniprot = runCrossReferenceProcess(context);
                }
            }

            // process the isoforms
            uniprot = processIsoforms(uniprot);
        }
        else {
            uniprot = runCrossReferenceProcess(context);
        }

        // Get the last report
        MappingReport report = this.listOfReports.get(this.listOfReports.size() - 1);

        // If Uniprot Protein API and Uniprot could mapp the identifier to an unique Uniprot accession
        if (uniprot != null){

            // if the accession is a Trembl accession
            if (!report.isASwissprotEntry()){

                // Get the Uniprot protein for this Trembl entry
                UniprotProtein tremblEntry = getUniprotProteinFor(uniprot);

                // Create a new blast context containing the ensembl gane of the Trembl entry
                BlastContext blastContext = new BlastContext(context);
                if (tremblEntry != null){
                    String sequence = tremblEntry.getSequence();

                    blastContext.setSequence(sequence);

                    // extract the ensembl gene of the Trembl entry
                    String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                    blastContext.setEnsemblGene(ensemblGene);
                }
                else {
                    throw new ActionProcessingException("We couldn't find any Uniprot entries which match this accession number " + uniprot);
                }

                // Try to do a Swissprot-remapping process
                String uniprot2 = this.listOfActions.get(2).runAction(blastContext);
                // process the isoforms
                uniprot2 = processIsoforms(uniprot2);

                // If the swissprot remapping process is successful, this action return the swissprot accession  instead of the Trembl accession
                if (uniprot2 != null){
                    uniprot = uniprot2;
                }
                // add the reports
                this.listOfReports.addAll(this.listOfActions.get(2).getListOfActionReports());

                List<BlastReport> listOfSwissprotRemappingReports = getSwissprotRemappingReports(this.listOfReports);

                for (BlastReport<BlastResults> sr : listOfSwissprotRemappingReports){
                    for (BlastResults r : sr.getBlastMatchingProteins()){
                        r.setTremblAccession(tremblEntry.getPrimaryAc());
                    }
                }
            }
        }
        return uniprot;
    }

    /**
     *
     * @return the list of reports of this object
     */
    public List<MappingReport> getListOfActionReports() {
        return this.listOfReports;
    }
}
