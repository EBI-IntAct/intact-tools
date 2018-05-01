package uk.ac.ebi.intact.tools.feature.shortlabel.generator.model;

/**
 * Created by anjali on 23/04/18.
 */
public class InsertionDataFeed {

    private boolean isInsertion;
    private String insertionString;
    private boolean toBeCuratedManually;


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

    public boolean isToBeCuratedManually() {
        return toBeCuratedManually;
    }

    public void setToBeCuratedManually(boolean toBeCuratedManually) {
        this.toBeCuratedManually = toBeCuratedManually;
    }
}
