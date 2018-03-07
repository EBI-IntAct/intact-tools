package uk.ac.ebi.intact.tools.feature.shortlabel.generator;

import uk.ac.ebi.intact.tools.feature.shortlabel.generator.impl.FeatureListener;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

    private ShortlabelGenerator getShortlabelGenerator(){
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/shortlabel-generator-config.xml");
        return context.getBean(ShortlabelGenerator.class);
    }
    
    @Test
    public void ShortlabelGeneratorTest_1(){
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        shortlabelGenerator.addListener(new FeatureListener());
          shortlabelGenerator.generateNewShortLabel("EBI-10921757");
    }

    @Test
    public void regexTest(){
        String oSequence="QLQQ";
        String pattern = oSequence.charAt(0)+"{"+oSequence.length()+"}";
        Pattern r = Pattern.compile(pattern);

        Matcher m=r.matcher(oSequence);
        boolean matched=m.matches();
    }

    @Test
    public void regexTest2(){
        String oSequence="QLQQ";
        String rSequence="QLQQQLQQQLPQQLQQ";
        Double remainder=new Double(rSequence.length()%oSequence.length());
        if(remainder==0d){
            int factor = rSequence.length()/oSequence.length();
            String pattern = "("+oSequence+")"+"{"+factor+"}";
            Pattern r = Pattern.compile(pattern);

            Matcher m=r.matcher(rSequence);
            boolean matched=m.matches();
            System.out.println(matched);
        }


    }
}
