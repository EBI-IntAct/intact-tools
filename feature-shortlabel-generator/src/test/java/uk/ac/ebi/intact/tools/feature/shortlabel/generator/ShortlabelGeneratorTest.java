package uk.ac.ebi.intact.tools.feature.shortlabel.generator;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.intact.tools.feature.shortlabel.generator.impl.FeatureListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maximilian Koch (mkoch@ebi.ac.uk).
 */
public class ShortlabelGeneratorTest {

    /**
     * This is playground. Please select the profile and insert a AC into the generateNewShortLabel() method.
     * You'll see which event's are triggered during the shortlabel generation.
     */

    private ShortlabelGenerator getShortlabelGenerator() {
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/shortlabel-generator-config.xml");
        return context.getBean(ShortlabelGenerator.class);
    }

    @Test
    public void ShortlabelGeneratorTest_1() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        shortlabelGenerator.addListener(new FeatureListener());
        /*polycules*/

        // shortlabelGenerator.generateNewShortLabel("EBI-10921757");
        // shortlabelGenerator.generateNewShortLabel("EBI-14690097");

        /* Single amino acid change*/

        // shortlabelGenerator.generateNewShortLabel("EBI-9095885");
        //shortlabelGenerator.generateNewShortLabel("EBI-8524086");
        //shortlabelGenerator.generateNewShortLabel("EBI-9825301");

        /*Multiple amino acid change, non-sequential positions:*/

       // shortlabelGenerator.generateNewShortLabel("EBI-9693147");
        //shortlabelGenerator.generateNewShortLabel(("EBI-10889784"));
        //shortlabelGenerator.generateNewShortLabel("EBI-15731927");

        /*Multiple amino acid change, sequential positions:*/
        //shortlabelGenerator.generateNewShortLabel("EBI-11178974");//? single AA change?
        //shortlabelGenerator.generateNewShortLabel("EBI-11314033");
        //shortlabelGenerator.generateNewShortLabel("EBI-12590047");
        //shortlabelGenerator.generateNewShortLabel("EBI-8839684");
        //shortlabelGenerator.generateNewShortLabel("EBI-9846491");
        //shortlabelGenerator.generateNewShortLabel("EBI-2891626");
        //shortlabelGenerator.generateNewShortLabel("EBI-15582875");

        //Second round

        //shortlabelGenerator.generateNewShortLabel("EBI-12687520");
       // shortlabelGenerator.generateNewShortLabel("EBI-11302770");
        //shortlabelGenerator.generateNewShortLabel("EBI-977116");
       // shortlabelGenerator.generateNewShortLabel("EBI-12687523");

        /*Deletion*/

        shortlabelGenerator.generateNewShortLabel("EBI-6898602");
        //shortlabelGenerator.generateNewShortLabel("EBI-16008622");
        //shortlabelGenerator.generateNewShortLabel("EBI-9085688");
        //shortlabelGenerator.generateNewShortLabel("EBI-1641252");

        /*Insertions*/

        // shortlabelGenerator.generateNewShortLabel("EBI-2891626");
       // shortlabelGenerator.generateNewShortLabel("EBI-11475055");

        // More cases

        //shortlabelGenerator.generateNewShortLabel("EBI-16022338");
        //shortlabelGenerator.generateNewShortLabel("EBI-16007653");
       // shortlabelGenerator.generateNewShortLabel("EBI-15938869");
       // shortlabelGenerator.generateNewShortLabel("EBI-13639830");
        // shortlabelGenerator.generateNewShortLabel("EBI-10761861");

    }

    @Test
    public void regexTest() {
        String oSequence = "QLQQ";
        String pattern = oSequence.charAt(0) + "{" + oSequence.length() + "}";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(oSequence);
        boolean matched = m.matches();
    }

    @Test
    public void regexTest2() {
        String oSequence = "QLQQ";
        String rSequence = "QLQQQLQQQLPQQLQQ";
        Double remainder = new Double(rSequence.length() % oSequence.length());
        if (remainder == 0d) {
            int factor = rSequence.length() / oSequence.length();
            String pattern = "(" + oSequence + ")" + "{" + factor + "}";
            Pattern r = Pattern.compile(pattern);

            Matcher m = r.matcher(rSequence);
            boolean matched = m.matches();
            System.out.println(matched);
        }


    }

    @Test
    public void regexTest3() {
        String oSequence = "QLQQPQ";
        String rSequence = "QL..T.";
        String pattern = "[\\.]";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(rSequence);
        //  boolean matched=m.matches();
        int count = 0;
        char[] rSequenceArray = rSequence.toCharArray();
        if (true) {
            while (m.find()) {
                count++;
                System.out.println("Match number " + count);
                System.out.println("start(): " + m.start());
                Character character = oSequence.toCharArray()[m.start()];
                rSequenceArray[m.start()] = character;
            }
        }
        String fabricatedRSequence = new String(rSequenceArray);
        boolean deletionInsertion = !fabricatedRSequence.equals(oSequence);
    }

    @Test
    public void regexTest4() {
        boolean isInsertion=false;
        String oSequence = "LL";
        String rSequence = "GGSGL";
        String pattern = ".*";
        char[] originalSeqArray=oSequence.toCharArray();
        for(char oSeqChar:originalSeqArray){
            pattern=pattern+"(?=.*"+oSeqChar+")";
        }
        pattern=pattern+".*";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(rSequence);
        boolean matched=m.matches();
        int count = 0;
         if (matched) {
            while (m.find()) {
                count++;

            }
        }

        if(count==oSequence.length()){
            isInsertion=true;
        }


    }


}
