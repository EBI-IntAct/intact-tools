package uk.ac.ebi.intact.protein.mapping.actions;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.factories.impl.DefaultReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultUniprotProteinAPIReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;

/**
 * Unit test for UniprotProteinAPISearchProcessWithAccession.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Apr-2010</pre>
 */

public class UniprotProteinAPISearchProcessWithAccessionTest {

    private UniprotProteinAPISearchProcessWithAccession process;
    private IdentificationContext context;

    public UniprotProteinAPISearchProcessWithAccessionTest() {
        this.process = new UniprotProteinAPISearchProcessWithAccession(new DefaultReportsFactory());
        this.context = new IdentificationContext();
    }

    private BioSource createBiosource(String shortLabel, String fullName, String taxId) {
        BioSource bioSource = new BioSource();
        bioSource.setFullName(fullName);
        bioSource.setShortLabel(shortLabel);
        bioSource.setTaxId(taxId);

        return bioSource;
    }

    @Test
    public void test_UniprotProteinAPIProcess_successful_withoutOrganism() throws ActionProcessingException {
        String id = "AAC02967";

        this.context.setIdentifier(id);
        this.context.setOrganism(null);


        String ac = this.process.runAction(context);
        List<MappingReport> reports = this.process.getListOfActionReports();

        for (String warn : reports.get(0).getWarnings()) {
            System.out.println(warn);
        }

        System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        Assert.assertNotNull(ac);
        Assert.assertTrue(reports.get(0) instanceof DefaultUniprotProteinAPIReport);
        Assert.assertEquals("Q71V77", ac);

    }

    @Test
    public void test_UniprotProteinAPIProcess_unsuccessful_withoutOrganism() throws ActionProcessingException {
        String id = "IPI00022256";

        this.context.setIdentifier(id);
        this.context.setOrganism(null);


        String ac = this.process.runAction(context);
        List<MappingReport> reports = this.process.getListOfActionReports();

        for (String warn : reports.get(0).getWarnings()) {
            System.out.println(warn);
        }

        System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        Assert.assertNull(ac);
        Assert.assertTrue(reports.get(0) instanceof DefaultUniprotProteinAPIReport);

    }

    @Test
    public void test_UniprotProteinAPIProcess_successful_withOrganism() throws ActionProcessingException {
        String id = "IPI00022256";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setIdentifier(id);
        this.context.setOrganism(organism);

        String ac = this.process.runAction(context);
        List<MappingReport> reports = this.process.getListOfActionReports();

        for (String warn : reports.get(0).getWarnings()) {
            System.out.println(warn);
        }

        System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        Assert.assertNotNull(ac);
        Assert.assertTrue(reports.get(0) instanceof DefaultUniprotProteinAPIReport);
        Assert.assertEquals("Q96CW1-1", ac);

    }

    @Test
    public void test_UniprotProteinAPIProcess_unSuccessful_withOrganism() throws ActionProcessingException {
        String id = "IPI00334775";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setIdentifier(id);
        this.context.setOrganism(organism);

        String ac = this.process.runAction(context);
        List<MappingReport> reports = this.process.getListOfActionReports();

        for (String warn : reports.get(0).getWarnings()) {
            System.out.println(warn);
        }

        System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        Assert.assertNull(ac);
        Assert.assertTrue(reports.get(0) instanceof DefaultUniprotProteinAPIReport);

    }
}
