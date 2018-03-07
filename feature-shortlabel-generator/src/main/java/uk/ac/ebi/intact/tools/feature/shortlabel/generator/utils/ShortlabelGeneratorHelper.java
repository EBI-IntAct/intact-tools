package uk.ac.ebi.intact.tools.feature.shortlabel.generator.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Polymer;
import psidev.psi.mi.jami.model.Range;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.AminoAcids;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.Constants;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.PolyculeDataFeed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String seq2ThreeLetterCodeOnDefaultResSeq(String sequence) {

        //example AlaAlaAlaAla

        String sequenceAsThreeLetterCode = "";
        for (int i = 0; i < sequence.length(); i++) {
            sequenceAsThreeLetterCode += AminoAcids.getThreeLetterCodeByOneLetterCode(sequence.charAt(i));
        }
        return sequenceAsThreeLetterCode;
    }

    public String seq2ThreeLetterCodeOnDefaultOrgSeq(String sequence, Long rangeStart, Long rangeEnd) {

        // example Pro12_Leu14 or Ile234

        String sequenceAsThreeLetterCode = "";
        Character startAA = sequence.charAt(0);
        Character endAA = null;

        sequenceAsThreeLetterCode += AminoAcids.getThreeLetterCodeByOneLetterCode(startAA);
        sequenceAsThreeLetterCode += rangeStart;
        if (sequence.length() > 1) {
            endAA = sequence.charAt(sequence.length() - 1);
            sequenceAsThreeLetterCode += Constants.ORG_SEQ_SEPERATOR + AminoAcids.getThreeLetterCodeByOneLetterCode(endAA) + rangeEnd;
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

    public boolean containsToManyDots(String resSeq) {
        return StringUtils.countMatches(resSeq, ".") > 3;
    }

    public void sortRanges(Range[] ranges, int startPos, int endPos) {
        int startCourser = startPos;
        int endCourser = endPos;

        Range pivotRange = ranges[endPos + (startPos - endPos) / 2];
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
        if (startPos < endCourser) {
            sortRanges(ranges, startPos, endCourser);
        }
        if (startCourser < endPos) {
            sortRanges(ranges, startCourser, endPos);
        }
    }

    private void swapRanges(Range[] ranges, int startCourser, int endCourser) {
        Range range = ranges[startCourser];
        ranges[startCourser] = ranges[endCourser];
        ranges[endCourser] = range;
    }

    public PolyculeDataFeed checkIfPolyculeAndReturnPDF(String oSequence, String rSequence) {

        boolean isPolycule = false;
        boolean isSingleAAPolycule = false;
        boolean isMultipleAAPolycule = false;
        int repeatUnit = 0;
        PolyculeDataFeed polyculeDataFeed = new PolyculeDataFeed();
        if (oSequence != null && rSequence != null) {
            if (rSequence.length() > oSequence.length()) {
                if (rSequence.contains(oSequence)) {
                    String pattern = oSequence.charAt(0) + "{" + oSequence.length() + "}";
                    Pattern r1 = Pattern.compile(pattern);

                    Matcher m = r1.matcher(oSequence);
                    boolean matched = m.matches();

                    if (matched) {
                        String resSeqPattern = oSequence.charAt(0) + "{" + rSequence.length() + "}";
                        Pattern r2 = Pattern.compile(resSeqPattern);
                        Matcher matcher = r2.matcher(rSequence);
                        boolean isResSeqMatched = matcher.matches();
                        isSingleAAPolycule = isResSeqMatched;
                        repeatUnit = rSequence.length();
                    } else {
                        Double remainder = new Double(rSequence.length() % oSequence.length());
                        if (remainder == 0d) {
                            int factor = rSequence.length() / oSequence.length();
                            String mAAPattern = "(" + oSequence + ")" + "{" + factor + "}";
                            Pattern r3 = Pattern.compile(mAAPattern);

                            Matcher matcher = r3.matcher(rSequence);
                            boolean isResSeqMatched = matcher.matches();
                            isMultipleAAPolycule = isResSeqMatched;
                            repeatUnit = factor;
                        }
                    }
                }
            }
        }
        if (isSingleAAPolycule || isMultipleAAPolycule) {
            isPolycule = true;
        }
        polyculeDataFeed.setMultipleAAPolycule(isMultipleAAPolycule);
        polyculeDataFeed.setSingleAAPolycule(isSingleAAPolycule);
        polyculeDataFeed.setPolycule(isPolycule);
        polyculeDataFeed.setRepeatUnit(repeatUnit);

        return polyculeDataFeed;
    }
}
