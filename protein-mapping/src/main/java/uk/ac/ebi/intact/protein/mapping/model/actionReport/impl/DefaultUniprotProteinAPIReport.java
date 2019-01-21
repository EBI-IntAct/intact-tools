package uk.ac.ebi.intact.protein.mapping.model.actionReport.impl;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.UniprotProteinAPIReport;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultUniprotProteinAPICrossReferences;

import java.util.HashSet;
import java.util.Set;

/**
 * This report aims at storing the information and results of a query on Uniprot Protein API
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */
public class DefaultUniprotProteinAPIReport extends DefaultMappingReport implements UniprotProteinAPIReport<DefaultUniprotProteinAPICrossReferences> {

    /**
     * the list of cross references that Uniprot Protein API could collect
     */
    private Set<DefaultUniprotProteinAPICrossReferences> crossReferences = new HashSet<DefaultUniprotProteinAPICrossReferences>();

    /**
     * Create a new DefaultUniprotProteinAPIReport
     * @param name : name of the action
     */
    public DefaultUniprotProteinAPIReport(ActionName name) {
        super(name);
    }

    /**
     *
     * @return the cross references
     */
    public Set<DefaultUniprotProteinAPICrossReferences> getCrossReferences(){
        return this.crossReferences;
    }

    /**
     * add a new cross reference
     * @param databaseName : database name
     * @param accession : accessions in the database
     */
    public void addCrossReference(String databaseName, String accession){
        boolean isADatabaseNamePresent = false;

        for (DefaultUniprotProteinAPICrossReferences c : this.crossReferences){
            if (c.getDatabase() != null){
                if (c.getDatabase().equalsIgnoreCase(databaseName)){
                    isADatabaseNamePresent = true;
                    c.addAccession(accession);
                }
            }
        }

        if (!isADatabaseNamePresent){
            DefaultUniprotProteinAPICrossReferences picrRefs = new DefaultUniprotProteinAPICrossReferences();
            picrRefs.setDatabase(databaseName);
            picrRefs.addAccession(accession);
        }
    }

    /**
     * Add a new UniprotProteinAPICrossReference instance to the list of references
     * @param refs : the UniprotProteinAPICrossReference instance to add
     */
    public void addUniprotProteinAPICrossReference(DefaultUniprotProteinAPICrossReferences refs){
         if (refs != null){
            this.crossReferences.add(refs);
         }
    }

    /**
     * Set the Uniprot Protein API cross references
     * @param crossReferences : set containing the Uniprot Protein API cross references
     */
    public void setCrossReferences(Set<DefaultUniprotProteinAPICrossReferences> crossReferences) {
        this.crossReferences = crossReferences;
    }

    @Override
    public boolean equals( Object o ) {
        return super.equals(o);
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        return super.hashCode();
    }

    @Override
    public boolean isIdenticalTo(Object o){

        if (!super.isIdenticalTo(o)){
            return false;
        }

        final DefaultUniprotProteinAPIReport report = (DefaultUniprotProteinAPIReport) o;

        return CollectionUtils.isEqualCollection(this.crossReferences, report.getCrossReferences());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(super.toString() + "\n");

        if (!crossReferences.isEmpty()){
            buffer.append("Uniprot Protein API references : [");

            for (DefaultUniprotProteinAPICrossReferences ref : crossReferences) {
                buffer.append(ref.toString() + " ; ");
            }

            buffer.append("\n");
        }

        return buffer.toString();
    }
}
