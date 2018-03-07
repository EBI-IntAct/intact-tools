package uk.ac.ebi.intact.tools.feature.shortlabel.generator.model;

/**
 * Created by Maximilian Koch (mkoch@ebi.ac.uk).
 */
public enum AminoAcids {
    ALANINE('A', "Ala"),
    ASPARAGINE_OR_ASPARTIC_ACID('B', "Asx"),
    CYSTEINE('C', "Cys"),
    ASPARTIC_ACID('D', "Asp"),
    GLUTAMIC_ACID('E', "Glu"),
    PHENYLALANINE('F', "Phe"),
    GLYCINE('G', "Gly"),
    HISTIDINE('H', "His"),
    ISOLEUCINE('I', "Ile"),
    LYSINE('K', "Lys"),
    LEUCINE('L', "Leu"),
    METHIONINE('M', "Met"),
    ASPARAGINE('N', "Asn"),
    SELENOCYSTEINE('U', "Sec"),
    PROLINE('P', "Pro"),
    GLUTAMINE('Q', "Gln"),
    ARGININE('R', "Arg"),
    SERINE('S', "Ser"),
    THREONINE('T', "Thr"),
    PYRROLYSINE('O', "Pyl"),
    VALINE('V', "Val"),
    TRYPTOPHAN('W', "Trp"),
    ANY_CODON('X', "Xaa"),
    TYROSINE('Y', "Tyr"),
    GLUTAMINE_OR_GLUTAMIC_ACID('Z', "Glx"),
    LEUCINE_OR_ISOLEUCINE('J',"Xle"),
    STOP_CODON('*', "*"),
    DELETION('.', "del");

    private Character oneLetterCode;
    private String threeLetterCode;

    AminoAcids(Character oneLetterCode, String threeLetterCode) {
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
    }

    public Character getOneLetterCode() {
        return oneLetterCode;
    }

    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    public static String getThreeLetterCodeByOneLetterCode(Character oneLetterCode) {
        for (AminoAcids aminoAcids : values()) {
            if (aminoAcids.getOneLetterCode() == oneLetterCode) {
                return aminoAcids.getThreeLetterCode();
            }
        }
        return null;
    }
}
