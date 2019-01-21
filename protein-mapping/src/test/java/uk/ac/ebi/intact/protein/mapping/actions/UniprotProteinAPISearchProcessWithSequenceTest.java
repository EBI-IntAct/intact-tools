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
 * Unit test for UniprotProteinAPISearchProcessWithSequence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Apr-2010</pre>
 */

public class UniprotProteinAPISearchProcessWithSequenceTest {

    private UniprotProteinAPISearchProcessWithSequence process;
    private IdentificationContext context;

    public UniprotProteinAPISearchProcessWithSequenceTest(){
        this.process = new UniprotProteinAPISearchProcessWithSequence(new DefaultReportsFactory());
        this.context = new IdentificationContext();
    }

    private BioSource createBiosource(String shortLabel, String fullName, String taxId){
        BioSource bioSource = new BioSource();
        bioSource.setFullName(fullName);
        bioSource.setShortLabel(shortLabel);
        bioSource.setTaxId(taxId);

        return bioSource;
    }

    @Test
    public void test_UniprotProteinAPIProcess_Successful_WithoutOrganism(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGIIP" +
                "ANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCVS" +
                "CDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQD" +
                "EFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVMT" +
                "QLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCEA" +
                "MEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASSTQDTGKLPVKWTAPEALREK" +
                "KFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMKN" +
                "CWHLDAAMRPSFLQLREQLEHIKTHELHL";

        this.context.setSequence(sequence);
        this.context.setOrganism(null);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultUniprotProteinAPIReport);
            Assert.assertEquals("P41240", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_UniprotProteinAPIProcess_Succssfull_WithOrganism(){
        String sequence = "MTDSKYFTTNKKGEIFELKAELNNEKKEKRKEAVKKVIAAMTVGKDVSSLFPDVVNCMQT" +
                "DNLELKKLVYLYLMNYAKSQPDMAIMAVNSFVKDCEDPNPLIRALAVRTMGCIRVDKITE" +
                "YLCEPLRKCLKDEDPYVRKTAAVCVAKLHDINAQMVEDQGFLDSLRDLIADSNPMVVANA" +
                "VAALSEISESHPNSNLLDLNPQNINKLLTALNECTEWGQIFILDCLSNYNPKDDREAQSI" +
                "CERVTPRLSHANSAVVLSAVKVLMKFLELLPKDSDYYNMLLKKLAPPLVTLLSGEPEVQY" +
                "VALRNINLIVQKRPEILKQEIKVFFVKYNDPIYVKLEKLDIMIRLASQANIAQVLAELKE" +
                "YATEVDVDFVRKAVRAIGRCAIKVEQSAERCVSTLLDLIQTKVNYVVQEAIVVIRDIFRK" +
                "YPNKYESIIATLCENLDSLDEPDARAAMIWIVGEYAERIDNADELLESFLEGFHDESTQV" +
                "QLTLLTAIVKLFLKKPSETQELVQQVLSLATQDSDNPDLRDRGYIYWRLLSTDPVTAKEV" +
                "VLSEKPLISEETDLIEPTLLDELICHIGSLASVYHKPPNAFVEGSHGIHRKHLPIHHGST" +
                "DAGDSPVGTTTATNLEQPQVIPSQGDLLGDLLNLDLGPPVNVPQVSSMQMGAVDLLGGGL" +
                "DSLLGSDLGGGIGGSPAVGQSFIPSSVPATFAPSPTPAVVSSGLNDLFELSTGIGMAPGG" +
                "YVAPKAVWLPAVKAKGLEISGTFTHRQGHIYMEMNFTNKALQHMTDFAIQFNKNSFGVIP" +
                "STPLAIHTPLMPNQSIDVSLPLNTLGPVMKMEPLNNLQVAVKNNIDVFYFSCLIPLNVLF" +
                "VEDGKMERQVFLATWKDIPNENELQFQIKECHLNADTVSSKLQNNNVYTIAKRNVEGQDM" +
                "LYQSLKLTNGIWILAELRIQPGNPNYTLSLKCRAPEVSQYIYQVYDSILKN";

        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        this.context.setSequence(sequence);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultUniprotProteinAPIReport);
            Assert.assertEquals("P63010-2", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_UniprotProteinAPIProcess_ToBeReviewd_WithoutOrganism(){
        String sequence = "MTDSKYFTTNKKGEIFELKAELNNEKKEKRKEAVKKVIAAMTVGKDVSSLFPDVVNCMQT" +
                "DNLELKKLVYLYLMNYAKSQPDMAIMAVNSFVKDCEDPNPLIRALAVRTMGCIRVDKITE" +
                "YLCEPLRKCLKDEDPYVRKTAAVCVAKLHDINAQMVEDQGFLDSLRDLIADSNPMVVANA" +
                "VAALSEISESHPNSNLLDLNPQNINKLLTALNECTEWGQIFILDCLSNYNPKDDREAQSI" +
                "CERVTPRLSHANSAVVLSAVKVLMKFLELLPKDSDYYNMLLKKLAPPLVTLLSGEPEVQY" +
                "VALRNINLIVQKRPEILKQEIKVFFVKYNDPIYVKLEKLDIMIRLASQANIAQVLAELKE" +
                "YATEVDVDFVRKAVRAIGRCAIKVEQSAERCVSTLLDLIQTKVNYVVQEAIVVIRDIFRK" +
                "YPNKYESIIATLCENLDSLDEPDARAAMIWIVGEYAERIDNADELLESFLEGFHDESTQV" +
                "QLTLLTAIVKLFLKKPSETQELVQQVLSLATQDSDNPDLRDRGYIYWRLLSTDPVTAKEV" +
                "VLSEKPLISEETDLIEPTLLDELICHIGSLASVYHKPPNAFVEGSHGIHRKHLPIHHGST" +
                "DAGDSPVGTTTATNLEQPQVIPSQGDLLGDLLNLDLGPPVNVPQVSSMQMGAVDLLGGGL" +
                "DSLLGSDLGGGIGGSPAVGQSFIPSSVPATFAPSPTPAVVSSGLNDLFELSTGIGMAPGG" +
                "YVAPKAVWLPAVKAKGLEISGTFTHRQGHIYMEMNFTNKALQHMTDFAIQFNKNSFGVIP" +
                "STPLAIHTPLMPNQSIDVSLPLNTLGPVMKMEPLNNLQVAVKNNIDVFYFSCLIPLNVLF" +
                "VEDGKMERQVFLATWKDIPNENELQFQIKECHLNADTVSSKLQNNNVYTIAKRNVEGQDM" +
                "LYQSLKLTNGIWILAELRIQPGNPNYTLSLKCRAPEVSQYIYQVYDSILKN";

        this.context.setSequence(sequence);
        this.context.setOrganism(null);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultUniprotProteinAPIReport);
            Assert.assertEquals(2, reports.get(0).getPossibleAccessions().size());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_UniprotProteinAPIProcess_Unsuccessful_WithoutOrganism(){
        String sequence = "MTDSKYFTTNKKGEIFELKAELNNEKKEKRKEAVKKVIAAMTVGKDVSSLFPDVVNCMQTDNLELKKLVYLYLMNYAKSQPDMAIMAVNSF" +
                "VKDCEDPNPLIRALAVRTMGCIRVDKITEYLCEPLRKCLKDEDPYVRKTAAVCVAKLHDINAQMVEDQGFLDSLRDLIADSNPMVVANAVAALSEISESHP" +
                "NSNLLDLNPQNINKLLTALNECTEWGQIFILDCLSNYNPKDDREAQSICERVTPRLSHANSAVVLSAVKVLMKFLELLPKDSDYYNMLLKKLAPPLVTLLS" +
                "GEPEVQYVALRNINLIVQKRPEILKQEIKVFFVKYNDPIYVKLEKLDIMIRLASQANIAQVLAELKEYATEVDVDFVRKAVRAIGRCAIKVEASQSAERCV" +
                "STLLDLIQTKVNYVVQEAIVVIRDIFRKYPNKYESIIATLCENLDSLDEPDARAAMIWIVGEYAERIDNADELLESFLEGFHDESTQVQLTLLTAIVKLFLK" +
                "KPSETQELVQQVLSLATQDSDNPDLRDRGYIYWRLLSTDPVTAKEVVLSEKPLISEETDLIEPTLLDELICHIGSLASVYHKPPNAFVEGSHGIHRKHLPI" +
                "HHGSTDAGDSPVGTTTATNLEQPQVIPSQGDLLGDLLNLDLGPPVNVPQVSSMQMGAVDLLGGGLDSLLGSDLGGGIGGSPAVGQSFIPSSVPATFAPSPT" +
                "PAVVSSGLNDLFELSTGIGMAPGGYVAPKAVWLPAVKAKGLEISGTFTHRQGHIYMEMNFTNKALQHMTDFAIQFNKNSFGVIPSTPLAIHTPLMPNQSID" +
                "VSLPLNTLGPVMKMEPLNNLQVAVKNNIDVFYFSCLIPLNVLFVEDGKMERQVFLATWKDIPNENELQFQIKECHLNADTVSSKLQNNNVYTIAKRNVEGQD" +
                "MLYQSLKLTNGIWILAELRIQPGNPNYTLSLKCRAPEVSQYIYQVYDSILKN";

        this.context.setSequence(sequence);
        this.context.setOrganism(null);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(2, reports.size());

            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(true, reports.get(1) instanceof DefaultUniprotProteinAPIReport);
            Assert.assertEquals(0, reports.get(1).getPossibleAccessions().size());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_UniprotProteinAPIProcess_Successful_WithOrganism_Trembl(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGIIPANYVQKREGVKAGTKLSLMPWFHGKITREQ" +
                "AERLLYPPETGLFLVRESTNYPGDYTLCVSCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQDEFYRSGWALNM" +
                "KELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVMTQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFS" +
                "LDVCEAMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASTQDTGKLPVKWTAPEALREKKFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPR" +
                "VEKGYKMDAPDGCPPAVYEVMKNCWHLDAAMRPSFLQLREQLEHIKTHELHL";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setSequence(sequence);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(2, reports.size());

            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals(true, reports.get(1) instanceof DefaultUniprotProteinAPIReport);
            Assert.assertEquals("Q53EL3", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
