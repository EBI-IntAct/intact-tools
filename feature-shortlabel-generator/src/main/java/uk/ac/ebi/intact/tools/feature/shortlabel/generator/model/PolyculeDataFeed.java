package uk.ac.ebi.intact.tools.feature.shortlabel.generator.model;

/**
 * Created by anjali on 07/03/18.
 */
public class PolyculeDataFeed {

    private boolean isPolycule;
    private boolean isSingleAAPolycule;
    private int repeatUnit;
    private boolean isMultipleAAPolycule;


    public boolean isPolycule() {
        return isPolycule;
    }

    public void setPolycule(boolean polycule) {
        isPolycule = polycule;
    }

    public boolean isSingleAAPolycule() {
        return isSingleAAPolycule;
    }

    public void setSingleAAPolycule(boolean singleAAPolycule) {
        isSingleAAPolycule = singleAAPolycule;
    }

    public int getRepeatUnit() {
        return repeatUnit;
    }

    public void setRepeatUnit(int repeatUnit) {
        this.repeatUnit = repeatUnit;
    }


    public boolean isMultipleAAPolycule() {
        return isMultipleAAPolycule;
    }

    public void setMultipleAAPolycule(boolean multipleAAPolycule) {
        isMultipleAAPolycule = multipleAAPolycule;
    }
}
