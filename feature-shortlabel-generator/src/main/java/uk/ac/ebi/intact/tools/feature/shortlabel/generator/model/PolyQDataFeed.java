package uk.ac.ebi.intact.tools.feature.shortlabel.generator.model;

/**
 * Created by anjali on 07/03/18.
 */
public class PolyQDataFeed {

    private boolean isPolyQ;
    private boolean isSingleAAPolyQ;
    private int repeatUnit;
    private boolean isMultipleAAPolyQ;



    public int getRepeatUnit() {
        return repeatUnit;
    }

    public void setRepeatUnit(int repeatUnit) {
        this.repeatUnit = repeatUnit;
    }


    public boolean isPolyQ() {
        return isPolyQ;
    }

    public void setPolyQ(boolean polyQ) {
        isPolyQ = polyQ;
    }

    public boolean isSingleAAPolyQ() {
        return isSingleAAPolyQ;
    }

    public void setSingleAAPolyQ(boolean singleAAPolyQ) {
        isSingleAAPolyQ = singleAAPolyQ;
    }

    public boolean isMultipleAAPolyQ() {
        return isMultipleAAPolyQ;
    }

    public void setMultipleAAPolyQ(boolean multipleAAPolyQ) {
        isMultipleAAPolyQ = multipleAAPolyQ;
    }
}
