package uk.ac.ebi.intact.protein.mapping.results.impl;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

/**
 * The annotated class containing the basic Blast results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-May-2010</pre>
 */
public class DefaultBlastResults implements BlastResults{

    private BlastProtein blastProtein;

    /**
     * the taxId of the protein identified in these blast results
     */
    private int taxId;

    /**
     * The trembl accession of the protein we tried to remap using the swissprot remapping process. Can be null if the blast has directly be
     * done on a anonymous sequence.
     */
    private String tremblAccession;

    /**
     * Create a new DefaultBlastResults instance
     */
    public DefaultBlastResults() {
        taxId = 0;
        this.tremblAccession = null;
        this.blastProtein = new BlastProtein();
    }

    /**
     * Create a new DefaultBlastResults instance from a previous BlastProtein instance
     * @param protein : the Blastprotein instance
     */
    public DefaultBlastResults(BlastProtein protein) {
        if( protein == null ) {
            throw new IllegalArgumentException( "You must give a non null protein" );
        }
        this.blastProtein = protein;
        this.tremblAccession = null;
    }

    /**
     *
     * @return The trembl accession we want to remap using the swissprto remapping process, null if it is a blast from anonymous sequence
     */
    public String getTremblAccession() {
        return tremblAccession;
    }

    /**
     * Set the trembl accession
     * @param tremblAccession
     */
    public void setTremblAccession(String tremblAccession) {
        this.tremblAccession = tremblAccession;
    }

    /**
     *
     * @return The taxId
     */
    public int getTaxId() {
        return taxId;
    }

    /**
     * Set the taxId
     * @param taxId
     */
    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    /**
     *
     * @return the accession of the protein
     */
    public String getAccession() {
        if (this.blastProtein != null){
            return this.blastProtein.getAccession();
        }
        return null;
    }

    /**
     *
     * @return the start position of the alignment in the query
     */
    public int getStartQuery() {
        if (this.blastProtein != null){
            return this.blastProtein.getStartQuery();
        }
        return 0;
    }

    /**
     *
     * @return the end position of the alignment in the query
     */
    public int getEndQuery() {
        if (this.blastProtein != null){
            return this.blastProtein.getEndQuery();
        }
        return 0;
    }

    /**
     * Set the start position of the alignment in the query
     * @param startQuery
     */
    public void setStartQuery(int startQuery) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setStartQuery(startQuery);
    }

    /**
     * Set the end position of the alignment in the query
     * @param endQuery
     */
    public void setEndQuery(int endQuery) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setEndQuery(endQuery);
    }

    /**
     *
     * @return The uniprot protein
     */
    public UniprotProtein getUniprotProtein() {
        if (this.blastProtein != null){
            return this.blastProtein.getUniprotProtein();
        }
        return null;
    }

    /**
     * Set the uniprot protein
     * @param prot
     */
    public void setUniprotProtein(UniprotProtein prot) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setUniprotProtein(prot);
    }

    /**
     *
     * @return The sequence
     */
    public String getSequence() {
        if (this.blastProtein != null){
            return this.blastProtein.getSequence();
        }
        return null;
    }

    /**
     *
     * @return  the database name
     */
    public String getDatabase() {
        if (this.blastProtein != null){
            return this.blastProtein.getDatabase();
        }
        return null;
    }

    /**
     *
     * @return the identity
     */
    public float getIdentity() {
        if (this.blastProtein != null){
            return this.blastProtein.getIdentity();
        }
        return 0;
    }

    /**
     *
     * @return the start match
     */
    public int getStartMatch() {
        if (this.blastProtein != null){
            return this.blastProtein.getStartMatch();
        }
        return 0;
    }

    /**
     *
     * @return  the end match
     */
    public int getEndMatch() {
        if (this.blastProtein != null){
            return this.blastProtein.getEndMatch();
        }
        return 0;
    }

    /**
     * Set the accession
     * @param accession
     */
    public void setAccession(String accession) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setAccession(accession);
    }

    /**
     * Set the sequence
     * @param sequence
     */
    public void setSequence(String sequence) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setSequence(sequence);
    }

    /**
     * Set the identity
     * @param identity
     */
    public void setIdentity(float identity) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setIdentity(identity);
    }

    /**
     * Set the database
     * @param database
     */
    public void setDatabase(String database) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setDatabase(database);
    }

    /**
     * Set the start match
     * @param startMatch
     */
    public void setStartMatch(int startMatch) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setStartMatch(startMatch);
    }

    /**
     * Set the end match
     * @param endMatch
     */
    public void setEndMatch(int endMatch) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setEndMatch(endMatch);
    }

    /**
     *
     * @return  the description
     */
    public String getDescription() {
        if (this.blastProtein != null){
            return this.blastProtein.getDescription();
        }
        return null;
    }

    /**
     * Set the description
     * @param description
     */
    public void setDescription(String description) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setDescription(description);
    }

    /**
     *
     * @return the alignment
     */
    public String getAlignment() {
        if (this.blastProtein != null){
            return this.blastProtein.getAlignment();
        }
        return null;
    }

    /**
     * Set the alignment
     * @param alignment
     */
    public void setAlignment(String alignment) {
        if (this.blastProtein == null){
            this.blastProtein = new BlastProtein();
        }

        this.blastProtein.setAlignment(alignment);
    }

    @Override
    public boolean equals( Object o ) {
        if ( !super.equals(o) ) {
            return false;
        }

        final DefaultBlastResults results = (DefaultBlastResults) o;

        if ( tremblAccession != null ) {
            if (!tremblAccession.equals( results.getTremblAccession() )){
                return false;
            }
        }
        else if (results.getTremblAccession()!= null){
            return false;
        }

        if (taxId != results.getTaxId()){
            return false;
        }

        if ( getAccession() != null ) {
            if (!getAccession().equals(results.getAccession())){
                return false;
            }
        }
        else if (results.getAccession()!= null){
            return false;
        }

        if (getStartQuery() != results.getStartQuery()){
            return false;
        }

        if (getEndQuery() != results.getEndQuery()){
            return false;
        }

        if (getStartMatch() != results.getStartMatch()){
            return false;
        }

        if (getEndMatch() != results.getEndMatch()){
            return false;
        }

        if ( getSequence() != null ) {
            if (!getSequence().equals(results.getSequence())){
                return false;
            }
        }
        else if (results.getSequence()!= null){
            return false;
        }

        if ( getDatabase() != null ) {
            if (!getDatabase().equals(results.getDatabase())){
                return false;
            }
        }
        else if (results.getDatabase()!= null){
            return false;
        }

        if (getIdentity() != results.getIdentity()){
            return false;
        }

        if ( getDescription() != null ) {
            if (!getDescription().equals(results.getDescription())){
                return false;
            }
        }
        else if (results.getDescription()!= null){
            return false;
        }

        if ( getAlignment() != null ) {
            if (!getAlignment().equals(results.getAlignment())){
                return false;
            }
        }
        else if (results.getAlignment()!= null){
            return false;
        }

        return true;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        int code = 29;

        code = 29 * code + super.hashCode();

        if ( tremblAccession != null ) {
            code = 29 * code + tremblAccession.hashCode();
        }

        code = 29 * code + Integer.toString(taxId).hashCode();

        if ( getAccession() != null ) {
            code = 29 * code + getAccession().hashCode();
        }

        code = 29 * code + Integer.toString(getStartQuery()).hashCode();

        code = 29 * code + Integer.toString(getEndQuery()).hashCode();

        code = 29 * code + Integer.toString(getStartMatch()).hashCode();

        code = 29 * code + Integer.toString(getEndMatch()).hashCode();

        if ( getSequence() != null ) {
            code = 29 * code + getSequence().hashCode();
        }

        if ( getDatabase() != null ) {
            code = 29 * code + getDatabase().hashCode();
        }

        code = 29 * code + Float.toString(getIdentity()).hashCode();

        if ( getDescription() != null ) {
            code = 29 * code + getDescription().hashCode();
        }

        if ( getAlignment() != null ) {
            code = 29 * code + getAlignment().hashCode();

        }

        return code;
    }

    public boolean isIdenticalTo(Object o){

        if (!(o instanceof DefaultBlastResults)){
            return false;
        }

        final DefaultBlastResults results = (DefaultBlastResults) o;

        if ( tremblAccession != null ) {
            if (!tremblAccession.equals( results.getTremblAccession() )){
                return false;
            }
        }
        else if (results.getTremblAccession()!= null){
            return false;
        }

        if (taxId != results.getTaxId()){
            return false;
        }

        if ( getAccession() != null ) {
            if (!getAccession().equals(results.getAccession())){
                return false;
            }
        }
        else if (results.getAccession()!= null){
            return false;
        }

        if (getStartQuery() != results.getStartQuery()){
            return false;
        }

        if (getEndQuery() != results.getEndQuery()){
            return false;
        }

        if (getStartMatch() != results.getStartMatch()){
            return false;
        }

        if (getEndMatch() != results.getEndMatch()){
            return false;
        }

        if ( getSequence() != null ) {
            if (!getSequence().equals(results.getSequence())){
                return false;
            }
        }
        else if (results.getSequence()!= null){
            return false;
        }

        if ( getDatabase() != null ) {
            if (!getDatabase().equals(results.getDatabase())){
                return false;
            }
        }
        else if (results.getDatabase()!= null){
            return false;
        }

        if (getIdentity() != results.getIdentity()){
            return false;
        }

        if ( getDescription() != null ) {
            if (!getDescription().equals(results.getDescription())){
                return false;
            }
        }
        else if (results.getDescription()!= null){
            return false;
        }

        if ( getAlignment() != null ) {
            if (!getAlignment().equals(results.getAlignment())){
                return false;
            }
        }
        else if (results.getAlignment()!= null){
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Blast Results : \n");

        if (tremblAccession != null){
            buffer.append("Trembl accession : " + tremblAccession);
            buffer.append("\n");
        }

        if (getDatabase() != null){
            buffer.append("Database : " + getDatabase());
            buffer.append("\n");
        }

        if (getAccession() != null){
            buffer.append("Accession : " + getAccession());
            buffer.append("\n");
        }

        buffer.append("TaxId : " + taxId);
        buffer.append("\n");

        buffer.append("Identity : " + getIdentity());
        buffer.append("\n");

        buffer.append("Query : " + getStartQuery() + " - " + getEndQuery());
        buffer.append("\n");

        buffer.append("Match : " + getStartMatch() + " - " + getEndMatch());
        buffer.append("\n");

        if (getAlignment() != null){
            buffer.append("Alignment : " + getAlignment());
            buffer.append("\n");
        }

        if (getSequence() != null){
            buffer.append("Match Sequence : " + getSequence());
            buffer.append("\n");
        }

        return buffer.toString();
    }
}

