package uk.ac.ebi.intact.tools.feature.shortlabel.generator;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Range;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.extension.ExperimentalRange;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.events.*;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.listener.ShortlabelGeneratorListener;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.manager.ShortlabelGeneratorManager;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.utils.OntologyServiceHelper;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.utils.ShortlabelGeneratorHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maximilian Koch (mkoch@ebi.ac.uk).
 */
public class ShortlabelGenerator {
    private final static String MUTATION_MI_ID = "MI:0118";
    private final static String CV_TERM_NO_MUTATION_UPDATE = "EBI-11795051";
    private final static String CV_TERM_NO_MUTATION_EXPORT = "EBI-11806127";
    private final static String CV_TERM_NO_UNIPROT_UPDATE = "EBI-607777";

    private final static int OLS_SEARCHING_DEPTH = 10;
    private final static int TRIES = 3;

    private static Set<String> allowedFeatureTypes = new HashSet<String>();
    private static CvTerm noMutationUpdateTerm;
    private static CvTerm noMutationExportTerm;
    private static CvTerm noUniprotUpdateTerm;
    private ShortlabelGeneratorManager manager = new ShortlabelGeneratorManager();
    private ShortlabelGeneratorHelper helper = new ShortlabelGeneratorHelper();
    private IntactDao intactDao;

    public ShortlabelGenerator() {
        initAllowedFeatureTypes();
    }

    private void initAllowedFeatureTypes() {
        allowedFeatureTypes.addAll(OntologyServiceHelper.getOntologyServiceHelper().getAssociatedMITerms(MUTATION_MI_ID, OLS_SEARCHING_DEPTH));
    }

    public void addListener(ShortlabelGeneratorListener shortlabelGeneratorListener) {
        manager.addListener(shortlabelGeneratorListener);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public IntactFeatureEvidence getFeatureEvidence(String ac, int tries) {

        IntactFeatureEvidence featureEvidence = intactDao.getFeatureEvidenceDao().getByAc(ac);
        if (featureEvidence == null && tries > 0) {
            tries--;
            featureEvidence = getFeatureEvidence(ac, tries);
        } else if (featureEvidence == null && tries == 0) {
            ObjRetrieveErrorEvent event = new ObjRetrieveErrorEvent(ac, null,
                    ObjRetrieveErrorEvent.ErrorType.UNABLE_TO_RETRIEVE_FEATURE);
            manager.fireOnRetrieveObjErrorEvent(event);
            return null;
        }
        return featureEvidence;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public void generateNewShortLabel(String ac) {
        IntactFeatureEvidence featureEvidence = getFeatureEvidence(ac, TRIES);
        generateNewShortLabel(featureEvidence);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public void generateNewShortLabel(IntactFeatureEvidence featureEvidence) {

        if (featureEvidence == null) {
            return;
        }

        String orgShortlabel = featureEvidence.getShortName();
        String featureAc = featureEvidence.getAc();
        String interactorAc;
        String interactorSeq;
        String interactorType;
        boolean noMutationUpdate = false;
        Collection<Range> ranges;

        IntactInteractor interactor = helper.getInteractorByFeatureEvidence(featureEvidence);

        if (noMutationUpdateTerm == null || noMutationExportTerm == null || noUniprotUpdateTerm == null) {
            noMutationUpdateTerm = getIntActCVTermNoMutationUpdate(TRIES);
            noMutationExportTerm = getIntActCVTermNoMutationExport(TRIES);
            noUniprotUpdateTerm = getIntActCVTermUniProtUpdate(TRIES);
            if (noMutationUpdateTerm == null || noMutationExportTerm == null || noUniprotUpdateTerm == null) {
                return;
            }
        }

        if (interactor == null) {
            ObjRetrieveErrorEvent event = new ObjRetrieveErrorEvent(featureAc, null, ObjRetrieveErrorEvent.ErrorType.UNABLE_RETRIEVE_INTERACTOR);
            manager.fireOnRetrieveObjErrorEvent(event);
            return;
        }

        interactorAc = interactor.getAc();
        interactorType = interactor.getInteractorType().getShortName();

        if (!interactorType.equals("protein") && !interactorType.equals("peptide")) {
            TypeErrorEvent event = new TypeErrorEvent(featureAc, interactorAc, TypeErrorEvent.ObjTypeErrorType.WRONG_INTERACTOR_TYPE);
            manager.fireOnObjectTypeErrorEvent(event);
            return;
        }

        for (Annotation annotation : interactor.getAnnotations()) {
            if (annotation.getTopic().equals(noUniprotUpdateTerm)) {
                AnnotationFoundEvent event = new AnnotationFoundEvent(featureAc, interactorAc, AnnotationFoundEvent.AnnotationType.NO_UNIPROT_UPDATE);
                manager.fireOnAnnotationFoundEvent(event);
                AnnotationFoundEvent event1 = new AnnotationFoundEvent(featureAc, interactorAc, AnnotationFoundEvent.AnnotationType.NO_MUTATION_EXPORT);
                manager.fireOnAnnotationFoundEvent(event1);
                return;
            }
        }

        interactorSeq = helper.getInteractorSeqByInteractor(interactor);

        if (interactorSeq == null) {
            ObjRetrieveErrorEvent event = new ObjRetrieveErrorEvent(featureAc, interactorAc, ObjRetrieveErrorEvent.ErrorType.UNABLE_RETRIEVE_INTERACTOR_SEQUENCE);
            manager.fireOnRetrieveObjErrorEvent(event);
            return;
        }

        if (!allowedFeatureTypes.contains(featureEvidence.getType().getMIIdentifier())) {
            TypeErrorEvent event = new TypeErrorEvent(featureAc, interactorAc, TypeErrorEvent.ObjTypeErrorType.WRONG_FEATURE_TYPE);
            manager.fireOnObjectTypeErrorEvent(event);
            return;
        }

        for (Annotation annotation : featureEvidence.getAnnotations()) {
            if (annotation.getTopic().equals(noMutationExportTerm)) {
                AnnotationFoundEvent event = new AnnotationFoundEvent(featureAc, interactorAc, AnnotationFoundEvent.AnnotationType.NO_MUTATION_EXPORT);
                manager.fireOnAnnotationFoundEvent(event);
                return;
            }
            if (annotation.getTopic().equals(noMutationUpdateTerm)) {
                AnnotationFoundEvent event = new AnnotationFoundEvent(featureAc, interactorAc, AnnotationFoundEvent.AnnotationType.NO_MUTATION_UPDATE);
                manager.fireOnAnnotationFoundEvent(event);
                noMutationUpdate = true;
            }
        }

        featureEvidence.setShortName("");
        if (featureEvidence.getRanges() == null || featureEvidence.getRanges().size() == 0) {
            RangeErrorEvent event = new RangeErrorEvent(featureAc, interactorAc, null, RangeErrorEvent.ErrorType.RANGE_NULL);
            manager.fireOnRangeErrorEvent(event);
            return;
        }

        ranges = featureEvidence.getRanges();
        ExperimentalRange[] experimentalRanges = ranges.toArray(new ExperimentalRange[ranges.size()]);

        helper.sortRanges(experimentalRanges, 0, experimentalRanges.length - 1);
        for (int index = 0; index <= experimentalRanges.length - 1; index++) {
            String newShortlabel = "";
            long rangeStart;
            long rangeEnd;
            String orgSeq;
            String resSeq;
            String calculatedOrgSeq;
            boolean isDeletion = false;

            String rangeAc = experimentalRanges[index].getAc();

            if (experimentalRanges[index].getStart().getStart() == 0) {
                RangeErrorEvent event = new RangeErrorEvent(featureAc, interactorAc, rangeAc, RangeErrorEvent.ErrorType.START_POS_ZERO);
                manager.fireOnRangeErrorEvent(event);
                return;
            }
            if (experimentalRanges[index].getStart().isPositionUndetermined()) {
                RangeErrorEvent event = new RangeErrorEvent(featureAc, interactorAc, rangeAc, RangeErrorEvent.ErrorType.START_POS_UNDETERMINED);
                manager.fireOnRangeErrorEvent(event);
                return;
            }
            if (experimentalRanges[index].getResultingSequence().getOriginalSequence() == null) {
                RangeErrorEvent event = new RangeErrorEvent(featureAc, interactorAc, rangeAc, RangeErrorEvent.ErrorType.ORG_SEQ_NULL);
                manager.fireOnRangeErrorEvent(event);
                return;
            }
            if (experimentalRanges[index].getResultingSequence().getNewSequence() == null) {
                RangeErrorEvent event = new RangeErrorEvent(featureAc, interactorAc, rangeAc, RangeErrorEvent.ErrorType.RES_SEQ_NULL);
                manager.fireOnRangeErrorEvent(event);
                return;
            }

            rangeStart = experimentalRanges[index].getStart().getStart();
            rangeEnd = experimentalRanges[index].getEnd().getEnd();
            orgSeq = experimentalRanges[index].getResultingSequence().getOriginalSequence();
            resSeq = experimentalRanges[index].getResultingSequence().getNewSequence();
            calculatedOrgSeq = helper.generateOrgSeq(interactorSeq, rangeStart, rangeEnd);

            if (calculatedOrgSeq == null) {
                SequenceErrorEvent event = new SequenceErrorEvent(featureAc, interactorAc, rangeAc, SequenceErrorEvent.ErrorType.UNABLE_CALCULATE_ORG_SEQ);
                manager.fireOnSeqErrorEvent(event);
                return;
            }
            if (helper.orgSeqWrong(orgSeq, calculatedOrgSeq)) {
                String message = "Original sequence does not match interactor sequence. Is " + orgSeq + " should be " + calculatedOrgSeq + " Range: (" + rangeStart + "-" + rangeEnd + ")";
                SequenceErrorEvent event = new SequenceErrorEvent(featureAc, interactorAc, rangeAc, SequenceErrorEvent.ErrorType.ORG_SEQ_WRONG, message);
                manager.fireOnSeqErrorEvent(event);
                return;
            }
            if (helper.containsLowerCaseLetters(resSeq)) {
                SequenceErrorEvent event = new SequenceErrorEvent(featureAc, interactorAc, rangeAc, SequenceErrorEvent.ErrorType.RES_SEQ_CONTAINS_LOWER_CASE);
                manager.fireOnSeqErrorEvent(event);
                return;
            }

            if (helper.resultingSeqDescreased(orgSeq, resSeq)) {
                SequenceErrorEvent event = new SequenceErrorEvent(featureAc, interactorAc, rangeAc, SequenceErrorEvent.ErrorType.RES_SEQ_SMALLER_ORG_SEQ);
                manager.fireOnSeqErrorEvent(event);
                return;
            }

            if (helper.containsDot(resSeq)) {
                if (helper.deletionOnWrongPlace(resSeq)) {
                    SequenceErrorEvent event = new SequenceErrorEvent(featureAc, interactorAc, rangeAc, SequenceErrorEvent.ErrorType.RES_SEQ_WITH_WRONG_DELETION);
                    manager.fireOnSeqErrorEvent(event);
                    return;
                }
                if (helper.containsToManyDots(resSeq)) {
                    SequenceErrorEvent event = new SequenceErrorEvent(featureAc, interactorAc, rangeAc, SequenceErrorEvent.ErrorType.RES_SEQ_TO_MANY_DOTS);
                    manager.fireOnSeqErrorEvent(event);
                    return;
                }
                isDeletion = true;
            }

            newShortlabel += helper.seq2ThreeLetterCodeOnDefault(orgSeq);

            if (helper.isSingleAminoAcidChange(rangeStart, rangeEnd)) {
                newShortlabel += helper.generateNonSequentialRange(rangeStart);
            } else {
                newShortlabel += helper.generateSequentialRange(rangeStart, rangeEnd);
            }

            if (isDeletion) {
                ResultingSequenceChangedEvent event = new ResultingSequenceChangedEvent(featureAc, interactorAc, rangeAc, ResultingSequenceChangedEvent.ChangeType.DELETION);
                manager.fireOnResSeqChangedEvent(event);
            } else if (helper.resultingSeqIncreased(orgSeq, resSeq)) {
                ResultingSequenceChangedEvent event = new ResultingSequenceChangedEvent(featureAc, interactorAc, rangeAc, ResultingSequenceChangedEvent.ChangeType.INCREASE);
                manager.fireOnResSeqChangedEvent(event);
            } else {
                ResultingSequenceChangedEvent event = new ResultingSequenceChangedEvent(featureAc,
                        interactorAc, rangeAc, ResultingSequenceChangedEvent.ChangeType.STABLE);
                manager.fireOnResSeqChangedEvent(event);
            }
            newShortlabel += helper.seq2ThreeLetterCodeOnDefault(resSeq);

            featureEvidence.setShortName(featureEvidence.getShortName() + newShortlabel + (index < experimentalRanges.length - 1 ? "," : ""));
        }
        if (noMutationUpdate || orgShortlabel.equals(featureEvidence.getShortName())) {
            UnmodifiedMutationShortlabelEvent event = new UnmodifiedMutationShortlabelEvent(featureAc, interactorAc, featureEvidence, noMutationUpdate);
            manager.fireOnUnmodifiedMutationShortlabelEvent(event);
        } else {
            ModifiedMutationShortlabelEvent event = new ModifiedMutationShortlabelEvent(featureAc, interactorAc, featureEvidence, orgShortlabel, false);
            manager.fireOnModifiedMutationShortlabelEvent(event);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    private CvTerm getIntActCVTermNoMutationUpdate(int tries) {
        CvTerm intactCvTerm = intactDao.getCvTermDao().getByAc(CV_TERM_NO_MUTATION_UPDATE);
        if (intactCvTerm == null && tries > 0) {
            tries--;
            intactCvTerm = getIntActCVTermNoMutationUpdate(tries);
        } else if (intactCvTerm == null && tries == 0) {
            ObjRetrieveErrorEvent event = new ObjRetrieveErrorEvent(null, null, ObjRetrieveErrorEvent.ErrorType.UNABLE_TO_RETRIEVE_CV_NO_MUTATION_UPDATE);
            manager.fireOnRetrieveObjErrorEvent(event);
            return null;
        }
        return intactCvTerm;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    private CvTerm getIntActCVTermNoMutationExport(int tries) {
        CvTerm intactCvTerm = intactDao.getCvTermDao().getByAc(CV_TERM_NO_MUTATION_EXPORT);
        if (intactCvTerm == null && tries > 0) {
            tries--;
            intactCvTerm = getIntActCVTermNoMutationUpdate(tries);
        } else if (intactCvTerm == null && tries == 0) {
            ObjRetrieveErrorEvent event = new ObjRetrieveErrorEvent(null, null, ObjRetrieveErrorEvent.ErrorType.UNABLE_TO_RETRIEVE_CV_NO_MUTATION_EXPORT);
            manager.fireOnRetrieveObjErrorEvent(event);
            return null;
        }
        return intactCvTerm;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    private CvTerm getIntActCVTermUniProtUpdate(int tries) {
        CvTerm intactCvTerm = intactDao.getCvTermDao().getByAc(CV_TERM_NO_UNIPROT_UPDATE);
        if (intactCvTerm == null && tries > 0) {
            tries--;
            intactCvTerm = getIntActCVTermUniProtUpdate(tries);
        } else if (intactCvTerm == null && tries == 0) {
            ObjRetrieveErrorEvent event = new ObjRetrieveErrorEvent(null, null, ObjRetrieveErrorEvent.ErrorType.UNABLE_TO_RETRIEVE_CV_NO_UNIPROT_UPDATE);
            manager.fireOnRetrieveObjErrorEvent(event);
            return null;
        }
        return intactCvTerm;
    }

    @Required
    public void setIntactDao(IntactDao intactDao) {
        this.intactDao = intactDao;
    }
}