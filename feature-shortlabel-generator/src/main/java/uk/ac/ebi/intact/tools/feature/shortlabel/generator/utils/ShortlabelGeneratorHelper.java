package uk.ac.ebi.intact.tools.feature.shortlabel.generator.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.Polymer;
import psidev.psi.mi.jami.model.Range;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.AminoAcids;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.Constants;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.InsertionDataFeed;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.model.PolyQDataFeed;

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

    public boolean isSingleChange(Long startPosition, Long endPosition) {
        return startPosition.equals(endPosition);
    }

    public boolean isSingleAAChange(String originalSequence, String resultingSequence, Long startPosition, Long endPosition) {
        return (isSingleChange(startPosition, endPosition)
                && resultingSequence.length() == 1 && !resultingSequence.equals("."));
    }

    public boolean resultingSeqDescreased(String originalSequence, String resultingSequence) {
        return originalSequence.length() > resultingSequence.length();
    }

    public boolean resultingSeqIncreased(String originalSequence, String resultingSequence) {
        return originalSequence.length() < resultingSequence.length();
    }

    public boolean isStable(String originalSequence, String resultingSequence) {
        return originalSequence.length() == resultingSequence.length();
    }

    public boolean isItDelInsCase(String oSequence, String rSequence) {
        boolean deletionInsertion = false;
        if (isStable(oSequence, rSequence)) {

            //check if it contains only dots
            String pattern = "\\." + "{" + rSequence.length() + "}";
            Pattern r1 = Pattern.compile(pattern);

            Matcher m = r1.matcher(rSequence);
            boolean matched = m.matches();

            if (!matched) {
                deletionInsertion = true;
            }

            /*Below is a perfectly running code for the case when you want to determine non dots are same or different from original sequence*/

            /*String pattern = "[\\.]";
            Pattern r = Pattern.compile(pattern);

            Matcher m = r.matcher(rSequence);
            char[] rSequenceArray = rSequence.toCharArray();
            while (m.find()) {
                Character character = oSequence.toCharArray()[m.start()];
                rSequenceArray[m.start()] = character;
            }

            String fabricatedRSequence = new String(rSequenceArray);
            if (!fabricatedRSequence.equals(oSequence)) {
                deletionInsertion = true;
            }*/
        } else if (resultingSeqIncreased(oSequence, rSequence)) {
            //cannot be insertion case as it already checked for it before the call of this method
            // if (rSequence.indexOf(oSequence) != 0) {
            deletionInsertion = true;
            //}
        } else if (rSequence.length() > 0 && resultingSeqDescreased(oSequence, rSequence)) {
            deletionInsertion = true;
        }

        return deletionInsertion;
    }

    public InsertionDataFeed isInsertionCase(String oSequence, String rSequence, long startPosition, long endPosition) {
        boolean insertionCase = false;
        boolean toBeCuratedManually = false;
        String insertedAA = "";
        if (resultingSeqIncreased(oSequence, rSequence)) {
            /*if (isSingleChange(startPosition, endPosition)) {
                if(rSequence.charAt(rSequence.length()-1)==oSequence.charAt(0)){ // insertion before single AA
                    insertionCase=true;
                    toBeCuratedManually=true;
                    insertedAA = rSequence.substring(0,rSequence.length()-1);
                }else if(rSequence.charAt(0)==oSequence.charAt(0)){ // insertion after single AA
                    insertionCase=true;
                    toBeCuratedManually=true;
                    insertedAA = rSequence.substring(1,rSequence.length());
                }
            }else*/
            if (oSequence.length() == 2 && rSequence.charAt(0) == oSequence.charAt(0) && rSequence.charAt(rSequence.length() - 1) == oSequence.charAt(1)) { // insertion between two AA
                insertionCase = true;
                insertedAA = rSequence.substring(1, rSequence.length() - 1);
            }
        }

        InsertionDataFeed insertionDataFeed = new InsertionDataFeed();
        insertionDataFeed.setInsertion(insertionCase);
        insertionDataFeed.setInsertionString(insertedAA);
        insertionDataFeed.setToBeCuratedManually(toBeCuratedManually);

        return insertionDataFeed;
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
        IntactInteractor intactInteractor = null;
        IntactInteractor featureInteractor = (IntactInteractor) intactFeatureEvidence.getParticipant().getInteractor();

        // In case of stable complex one has to get Interactor from Range Participant
        if (featureInteractor.getInteractorType().getShortName().equals("stable complex")) {
            if (intactFeatureEvidence.getRanges() != null && !intactFeatureEvidence.getRanges().isEmpty()) {
                Entity entity = intactFeatureEvidence.getRanges().iterator().next().getParticipant();
                if (entity != null) {
                    intactInteractor = (IntactInteractor) intactFeatureEvidence.getRanges().iterator().next().getParticipant().getInteractor();
                }
            }
        } else {
            intactInteractor = featureInteractor;
        }
        return intactInteractor;
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
            char seqChar = sequence.charAt(i);
            if (seqChar != '.') {
                sequenceAsThreeLetterCode += AminoAcids.getThreeLetterCodeByOneLetterCode(sequence.charAt(i));
            }
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

    public PolyQDataFeed checkIfPoyQAndReturnPDF(String oSequence, String rSequence) {

        boolean isPolycule = false;
        boolean isSingleAAPolycule = false;
        boolean isMultipleAAPolycule = false;
        int repeatUnit = 0;
        PolyQDataFeed polyQDataFeed = new PolyQDataFeed();
        String removedNewLinesRSeq = rSequence.replaceAll("\\n", "");
        removedNewLinesRSeq = removedNewLinesRSeq.replaceAll("\\r", "");
        if (oSequence != null && removedNewLinesRSeq != null) {
            if (removedNewLinesRSeq.length() > oSequence.length()) {
                if (removedNewLinesRSeq.contains(oSequence)) {
                    String pattern = oSequence.charAt(0) + "{" + oSequence.length() + "}";
                    Pattern r1 = Pattern.compile(pattern);

                    Matcher m = r1.matcher(oSequence);
                    boolean matched = m.matches();

                    if (matched) {
                        String resSeqPattern = oSequence.charAt(0) + "{" + removedNewLinesRSeq.length() + "}";
                        Pattern r2 = Pattern.compile(resSeqPattern);
                        Matcher matcher = r2.matcher(removedNewLinesRSeq);
                        boolean isResSeqMatched = matcher.matches();
                        isSingleAAPolycule = isResSeqMatched;
                        repeatUnit = removedNewLinesRSeq.length();
                    } else {
                        Double remainder = new Double(removedNewLinesRSeq.length() % oSequence.length());
                        if (remainder == 0d) {
                            int factor = removedNewLinesRSeq.length() / oSequence.length();
                            String mAAPattern = "(" + oSequence + ")" + "{" + factor + "}";
                            Pattern r3 = Pattern.compile(mAAPattern);

                            Matcher matcher = r3.matcher(removedNewLinesRSeq);
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
        polyQDataFeed.setMultipleAAPolyQ(isMultipleAAPolycule);
        polyQDataFeed.setSingleAAPolyQ(isSingleAAPolycule);
        polyQDataFeed.setPolyQ(isPolycule);
        polyQDataFeed.setRepeatUnit(repeatUnit);

        return polyQDataFeed;
    }
}
