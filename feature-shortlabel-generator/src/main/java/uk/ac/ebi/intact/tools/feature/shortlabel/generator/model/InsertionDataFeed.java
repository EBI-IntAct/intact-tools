package uk.ac.ebi.intact.tools.feature.shortlabel.generator.model;

/**
 * Created by anjali on 23/04/18.
 */
public class InsertionDataFeed {

    private boolean isInsertion;
    private String insertionString;


    public boolean isInsertion() {
        return isInsertion;
    }

    public void setInsertion(boolean insertion) {
        isInsertion = insertion;
    }

    public String getInsertionString() {
        return insertionString;
    }

    public void setInsertionString(String insertionString) {
        this.insertionString = insertionString;
    }
}
