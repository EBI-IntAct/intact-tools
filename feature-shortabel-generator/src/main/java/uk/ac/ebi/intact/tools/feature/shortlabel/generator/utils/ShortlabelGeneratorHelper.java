package uk.ac.ebi.intact.tools.feature.shortlabel.generator.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Polymer;
import psidev.psi.mi.jami.model.Range;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.AminoAcids;

/**
 * Created by Maximilian Koch (mkoch@ebi.ac.uk).
 */
public class ShortlabelGeneratorHelper {

    public boolean orgSeqWrong(String originalSequence, String newGeneratedOriginalSequence) {
        return !originalSequence.equals(newGeneratedOriginalSequence);
    }

    public boolean containsLowerCaseLetters(String resultingSequence) {
        return !resultingSequence.equals(resultingSequence.toUpperCase());
    }

    public boolean isSingleAminoAcidChange(Long startPosition, Long endPosition) {
        return startPosition.equals(endPosition);
    }

    public boolean resultingSeqDescreased(String originalSequence, String resultingSequence) {
        return originalSequence.length() > resultingSequence.length();
    }

    public boolean resultingSeqIncreased(String originalSequence, String resultingSequence) {
        return originalSequence.length() < resultingSequence.length();
    }

    public String generateNonSequentialRange(Long startingPosition) {
        //Non sequential position are like: ile345thr
        return String.valueOf(startingPosition);
    }

    public String generateSequentialRange(Long startingPosition, Long endPosition) {
        //sequential position are like: pro_thr_leu12-14ala_ala_pro
        return startingPosition + "-" + endPosition;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public String generateOrgSeq(String interactorSequence, Long startingPosition, Long endPosition) {
        //Check if the displayed original sequence of a feature, still matches with the whole sequence.
        String originalSequence = "";
        while (startingPosition.intValue() <= endPosition.intValue()) {
            originalSequence += interactorSequence.charAt(startingPosition.intValue() - 1);
            startingPosition++;
        }
        return originalSequence;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public String getInteractorSeqByInteractor(IntactInteractor intactInteractor) {
        //A polymer is an interactor with a sequence
        try {
            return ((Polymer) intactInteractor).getSequence();
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public IntactInteractor getInteractorByFeatureEvidence(IntactFeatureEvidence intactFeatureEvidence) {
        //To get the whole sequence of a feature, we need to get the interactor
        return (IntactInteractor) intactFeatureEvidence.getParticipant().getInteractor();
    }

    public String seq2ThreeLetterCodeOnDefault(String sequence) {
        String sequenceAsThreeLetterCode = "";
        for (int i = 0; i < sequence.length(); i++) {
            sequenceAsThreeLetterCode += AminoAcids.getThreeLetterCodeByOneLetterCode(sequence.charAt(i));
            if (i < sequence.length() - 1) {
                sequenceAsThreeLetterCode += "_";
            }
        }
        return sequenceAsThreeLetterCode;
    }

    public boolean containsDot(String newSequence) {
        return newSequence.contains(".");
    }

    public boolean containsDot(char newSequence) {
        return newSequence == '.';
    }

    public boolean deletionOnWrongPlace(String resSeq) {
        char[] chars = resSeq.toCharArray();
        boolean foundLetter = false;
        for (int pos = chars.length - 1; 0 <= pos; pos--) {
            if (!containsDot(chars[pos])) {
                foundLetter = true;
            } else if (foundLetter && !containsDot(chars[pos])) {
                return true;
            }
        }
        return false;
    }

    public boolean containsToManyDots(String resSeq){
      return StringUtils.countMatches(resSeq, ".") > 3;
    }

    public void sortRanges(Range[] ranges, int startPos, int endPos){
        int startCourser = startPos;
        int endCourser = endPos;

        Range pivotRange = ranges[endPos +(startPos - endPos)/2];
        while (startCourser <= endCourser) {
            while (ranges[startCourser].getStart().getStart() < pivotRange.getStart().getStart()) {
                startCourser++;
            }
            while (ranges[endCourser].getStart().getStart() > pivotRange.getStart().getStart()) {
                endCourser--;
            }
            if (startCourser <= endCourser) {
                swapRanges(ranges, startCourser, endCourser);
                startCourser++;
                endCourser--;
            }
        }
        if (startPos < endCourser){
            sortRanges(ranges, startPos, endCourser);
        }
        if (startCourser < endPos){
            sortRanges(ranges, startCourser, endPos);
        }
    }

    private void swapRanges(Range[] ranges, int startCourser, int endCourser) {
        Range range = ranges[startCourser];
        ranges[startCourser] = ranges[endCourser];
        ranges[endCourser] = range;
    }
}
