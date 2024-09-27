package uk.ac.ebi.intact.tools.feature.shortlabel.generator.events;

public class OtherErrorEvent {
    private final String featureAc;
    private final ErrorType errorType;
    private final String errorDetails;

    public OtherErrorEvent(String featureAc, ErrorType errorType, String errorDetails) {
        this.featureAc = (featureAc == null) ? "undefined" : featureAc;
        this.errorType = errorType;
        this.errorDetails = errorDetails;
    }

    public String getFeatureAc() {
        return featureAc;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public enum ErrorType {
        SHORT_LABEL_TOO_LONG("New short label is too long");

        private final String message;

        ErrorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
