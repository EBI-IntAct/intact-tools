package uk.ac.ebi.intact.tools.feature.shortlabel.generator;

import uk.ac.ebi.intact.tools.feature.shortlabel.generator.impl.FeatureListener;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
            shortlabelGenerator.generateNewShortLabel("EBI-6597646");
    }
}
