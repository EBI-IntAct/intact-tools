package uk.ac.ebi.intact.tools.feature.shortlabel.generator.events;

/**
 * Created by Maximilian Koch (mkoch@ebi.ac.uk).
 */
public class ResultingSequenceChangedEvent {
    private String featureAc;
    private String interactorAc;
    private String rangeAc;
    private ChangeType changeType;
    private String message;
    private String oSeq;
    private String rSeq;
    private long rangeStart;
    private long rangeEnd;

    public ResultingSequenceChangedEvent(String featureAc, String interactorAc, String rangeAc,String oSeq,String rSeq,long rangeStart,long rangeEnd,ChangeType change) {
        this.featureAc = (featureAc == null) ? "undefined" : featureAc;
        this.interactorAc = (interactorAc == null) ? "undefined" : interactorAc;
        this.rangeAc = (rangeAc == null) ? "undefined" : rangeAc;
        this.changeType = change;
        this.message = change.getMessage();
        this.oSeq=oSeq;
        this.rSeq=rSeq;
        this.rangeStart=rangeStart;
        this.rangeEnd=rangeEnd;

    }

    public String getFeatureAc() {
        return featureAc;
    }

    public String getInteractorAc() {
        return interactorAc;
    }

    public String getRangeAc() {
        return rangeAc;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getMessage() {
        return message;
    }

    public String getoSeq() {
        return oSeq;
    }

    public String getrSeq() {
        return rSeq;
    }

    public long getRangeStart() {
        return rangeStart;
    }

    public long getRangeEnd() {
        return rangeEnd;
    }


    public enum ChangeType {
        DELETION("Resulting sequence contains deletions"),
        DELETION_INSERTION("Resulting Sequence contains deletion and substitutions"),
        INCREASE("Resulting sequence has increased"),
        DECREASE("Resulting sequence has decreased"),
        STABLE("Resulting sequence hasn't changed in length"),
        WRONG_INSERTION("Wrong Insertion Case, Has to be curated manually");

        private String message;

        ChangeType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
