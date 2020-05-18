package uk.ac.ebi.intact.tools.feature.shortlabel.generator;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.impl.DefaultCvTerm;
import psidev.psi.mi.jami.model.impl.DefaultParticipantEvidence;
import psidev.psi.mi.jami.model.impl.DefaultPosition;
import psidev.psi.mi.jami.model.impl.DefaultResultingSequence;
import uk.ac.ebi.intact.jami.model.extension.ExperimentalRange;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactProtein;
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
    public void shortlabelGeneratorTest_polycules_1() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-10921757",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("- (mutation increasing strength)");
        featureType.setMIIdentifier("MI:0119");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MATLEKLMKAFESLKSFQQQQQQQQQQQQQQQQQQQQQPPPPPPPPPPPQLPQPPPQAQPLLPQPQPPPPPPPPPPGPAVAEEPLHRPKKELSATKKDRVNHCLTICENIVAQSVRNSPEFQKLLGIAMELFLLCSDDAESDVRMVADECLNKVIKALMDSNLPRLQLELYKEIKKNGAPRSLRAALWRFAELAHLVRPQKCRPYLVNLLPCLTRTSKRPEESVQETLAAAVPKIMASFGNFANDNEIKVLLKAFIANLKSSSPTIRRTAAGSAVSICQHSRRTQYFYSWLLNVLLGLLVPVEDEHSTLLILGVLLTLRYLVPLLQQQVKDTSLKGSFGVTRKEMEVSPSAEQLVQVYELTLHHTQHQDHNVVTGALELLQQLFRTPPPELLQTLTAVGGIGQLTAAKEESGGRSRSGSIVELIAGGGSSCSPVLSRKQKGKVLLGEEEALEDDSESRSDVSSSALTASVKDEISGELAASSGVSTPGSAGHDIITEQPRSQHTLQADSVDLASCDLTSSATDGDEEDILSHSSSQVSAVPSDPAMDLNDGTQASSPISDSSQTTTEGPDSAVTPSDSSEIVLDGTDNQYLGLQIGQPQDEDEEATGILPDEASEAFRNSSMALQQAHLLKNMSHCRQPSDSSVDKFVLRDEATEPGDQENKPCRIKGDIGQSTDDDSAPLVHCVRLLSASFLLTGGKNVLVPDRDVRVSVKALALSCVGAAVALHPESFFSKLYKVPLDTTEYPEEQYVSDILNYIDHGDPQVRGATAILCGTLICSILSRSRFHVGDWMGTIRTLTGNTFSLADCIPLLRKTLKDESSVTCKLACTAVRNCVMSLCSSSYSELGLQLIIDVLTLRNSSYWLVRTELLETLAEIDFRLVSFLEAKAENLHRGAHHYTGLLKLQERVLNNVVIHLLGDEDPRVRHVAAASLIRLVPKLFYKCDQGQADPVVAVARDQSSVYLKLLMHETQPPSHFSVSTITRIYRGYNLLPSITDVTMENNLSRVIAAVSHELITSTTRALTFGCCEALCLLSTAFPVCIWSLGWHCGVPPLSASDESRKSCTVGMATMILTLLSSAWFPLDLSAHQDALILAGNLLAASAPKSLRSSWASEEEANPAATKQEEVWPALGDRALVPMVEQLFSHLLKVINICAHVLDDVAPGPAIKAALPSLTNPPSLSPIRRKGKEKEPGEQASVPLSPKKGSEASAASRQSDTSGPVTTSKSSSLGSFYHLPSYLKLHDVLKATHANYKVTLDLQNSTEKFGGFLRSALDVLSQILELATLQDIGKCVEEILGYLKSCFSREPMMATVCVQQLLKTLFGTNLASQFDGLSSNPSKSQGRAQRLGSSSVRPGLYHYCFMAPYTHFTQALADASLRNMVQAEQENDTSGWFDVLQKVSTQLKTNLTSVTKNRADKNAIHNHIRLFEPLVIKALKQYTTTTCVQLQKQVLDLLAQLVQLRVNYCLLDSDQVFIGFVLKQFEYIEVGQFRESEAIIPNIFFFLVLLSYERYHSKQIIGIPKIIQLCDGIMASGRKAVTHAIPALQPIVHDLFVLRGTNKADAGKELETQKEVVVSMLLRLIQYHQVLEMFILVLQQCHKENEDKWKRLSRQIADIILPMLAKQQMHIDSHEALGVLNTLFEILAPSSLRPVDMLLRSMFVTPNTMASVSTVQLWISGILAILRVLISQSTEDIVLSRIQELSFSPYLISCTVINRLRDGDSTSTLEEHSEGKQIKNLPEETFSRFLLQLVGILLEDIVTKQLKVEMSEQQHTFYCQELGTLLMCLIHIFKSGMFRRITAAATRLFRSDGCGGSFYTLDSLNLRARSMITTHPALVLLWCQILLLVNHTDYRWWAEVQQTPKRHSLSSTKLLSPQMSGEEEDSDLAAKLGMCNREIVRRGALILFCDYVCQNLHDSEHLTWLIVNHIQDLISLSHEPPVQDFISAVHRNSAASGLFIQAIQSRCENLSTPTMLKKTLQCLEGIHLSQSGAVLTLYVDRLLCTPFRVLARMVDILACRRVEMLLAANLQSSMAQLPMEELNRIQEYLQSSGLAQRHQRLYSLLDRFRLSTMQDSLSPSPPVSSHPLDGDGHVSLETVSPDKDWYVHLVKSQCWTRSDSALLEGAELVNRIPAEDMNAFMMNSEFNLSLLAPCLSLGMSEISGGQKSALFEAAREVTLARVSGTVQQLPAVHHVFQPELPAEPAAYWSKLNDLFGDAALYQSLPTLARALAQYLVVVSKLPSHLHLPPEKEKDIVKFVVATLEALSWHLIHEQIPLSLDLQAGLDCCCLALQLPGLWSVVSSTEFVTHACSLIYCVHFILEAVAVQPGEQLLSPERRTNTPKAISEEEEEVDPNTQNPKYITAACEMVAEMVESLQSVLALGHKRNSGVPAFLTPLLRNIIISLARLPLVNSYTRVPPLVWKLGWSPKPGGDFGTAFPEIPVEFLQEKEVFKEFIYRINTLGWTSRTQFEETWATLLGVLVTQPLVMEQEESPPEEDTERTQINVLAVQAITSLVLSAMTVPVAGNPAVSCLEQQPRNKPLKALDTRFGRKLSIIRGIVEQEIQAMVSKRENIATHHLYQAWDPVPSLSPATTGALISHEKLLLQINPERELGSMSYKLGQVSIHSVWLGNSITPLREEEWDEEEEEEADAPAPSSPPTSPVNSRKHRAGVDIHSCSQFLLELYSRWILPSSSARRTPAILISEVVRSLLVVSDLFTERNQFELMYVTLTELRRVHPSEDEILAQYLVPATCKAAAVLGMDKAVAEPVSRLLESTLRSSHLPSRVGALHGVLYVLECDLLDDTAKQLIPVISDYLLSNLKGIAHCVNIHSQQHVLVMCATAFYLIENYPLDVGPEFSASIIQMCGVMLSGSEESTPSIIYHCALRGLERLLLSEQLSRLDAESLVKLSVDRVNVHSPHRAMAALGLMLTCMYTGKEKVSPGRTSDPNPAAPDSESVIVAMERVSVLFDRIRKGFPCEARVVARILPQFLDDFFPPQDIMNKVIGEFLSNQQPYPQFMATVVYKVFQTLHSTGQSSMVRDWVMLSLSNFTQRAPVAMATWSLSCFFVSASTSPWVAAILPHVISRMGKLEQVDVNLFCLVATDFYRHQIEEELDRRAFQSVLEVVAAPGSPYHRLLTCLRNVHKVTTC");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(18);
        Position end1 = new DefaultPosition(38);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("QQQQQQQQQQQQQQQQQQQQQ", "QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);
        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Gln18[97]", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_polycules_2() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        // IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-14690097",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("- (mutation increasing strength)");
        featureType.setMIIdentifier("MI:1132");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MKTRQNKDSMSMRSGRKKEAPGPREELRSRGRASPGGVSTSSSDGKAEKSRQTAKKARVEEASTPKVNKQGRSEEISESESEETNAPKKTKTEQELPRPQSPSDLDSLDGRSLNDDGSSDPRDIDQDNRSTSPSIYSPGSVENDSDSSSGLSQGPARPYHPPPLFPPSPQPPDSTPRQPEASFEPHPSVTPTGYHAPMEPPTSRMFQAPPGAPPPHPQLYPGGTGGVLSGPPMGPKGGGAASSVGGPNGGKQHPPPTTPISVSSSGASGAPPTKPPTTPVGGGNLPSAPPPANFPHVTPNLPPPPALRPLNNASASPPGLGAQPLPGHLPSPHAMGQGMGGLPPGPEKGPTLAPSPHSLPPASSSAPAPPMRFPYSSSSSSSAAASSSSSSSSSSASPFPASQALPSYPHSFPPPTSLSVSNQPPKYTQPSLPSQAVWSQGPPPPPPYGRLLANSNAHPGPFPPSTGAQSTAHPPVSTHHHHHQQQQQQQQQQQQQQQQQQQHHGNSGPPPPGAFPHPLEGGSSHHAHPYAMSPSLGSLRPYPPGPAHLPPPHSQVSYSQAGPNGPPVSSSSNSSSSTSQGSYPCSHPSPSQGPQGAPYPFPPVPTVTTSSATLSTVIATVASSPAGYKTASPPGPPPYGKRAPSPGAYKTATPPGYKPGSPPSFRTGTPPGYRGTSPPAGPGTFKPGSPTVGPGPLPPAGPSGLPSLPPPPAAPASGPPLSATQIKQEPAEEYETPESPVPPARSPSPPPKVVDVPSHASQSARFNKHLDRGFNSCARSDLYFVPLEGSKLAKKRADLVEKVRREAEQRAREEKEREREREREKEREREKERELERSVKLAQEGRAPVECPSLGPVPHRPPFEPGSAVATVPPYLGPDTPALRTLSEYARPHVMSPGNRNHPFYVPLGAVDPGLLGYNVPALYSSDPAAREREREARERDLRDRLKPGFEVKPSELEPLHGVPGPGLDPFPRHGGLALQPGPPGLHPFPFHPSLGPLERERLALAAGPALRPDMSYAERLAAERQHAERVAALGNDPLARLQMLNVTPHHHQHSHIHSHLHLHQQDAIHAASASVHPLIDPLASGSHLTRIPYPAGTLPNPLLPHPLHENEVLRHQLFAAPYRDLPASLSAPMSAAHQLQAMHAQSAELQRLALEQQQWLHAHHPLHSVPLPAQEDYYSHLKKESDKPL");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        CvTerm cvTerm = new DefaultCvTerm("protein");
        interactor.setInteractorType(cvTerm);
        Position start1 = new DefaultPosition(484, 484);
        Position end1 = new DefaultPosition(502, 502);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("QQQQQQQQQQQQQQQQQQQ", "QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);
        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Gln484[71]", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_SingleAminoAcidChange() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // can be tested with "EBI-8524086" and "EBI-9825301"
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-9095885",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation decreasing)");
        featureType.setMIIdentifier("MI:0119");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MAAAAGGGGPGTAVGATGSGIAAAAAGLAVYRRKDGGPATKFWESPETVSQLDSVRVWLGKHYKKYVHADAPTNKTLAGLVVQLLQFQEDAFGKHVTNPAFTKLPAKCFMDFKAGGALCHILGAAYKYKNEQGWRRFDLQNPSRMDRNVEMFMNIEKTLVQNNCLTRPNIYLIPDIDLKLANKLKDIIKRHQGTFTDEKSKASHHIYPYSSSQDDEEWLRPVMRKEKQVLVHWGFYPDSYDTWVHSNDVDAEIEDPPIPEKPWKVHVKWILDTDIFNEWMNEEDYEVDENRKPVSFRQRISTKNEEPVRSPERRDRKASANARKRKHSPSPPPPTPTESRKKSGKKGQASLYGKRRSQKEEDEQEDLTKDMEDPTPVPNIEEVVLPKNVNLKKDSENTPVKGGTVADLDEQDEETVTAGGKEDEDPAKGDQSRSVDLGEDNVTEQTNHIIIPSYASWFDYNCIHVIERRALPEFFNGKNKSKTPEIYLAYRNFMIDTYRLNPQEYLTSTACRRNLTGDVCAVMRVHAFLEQWGLVNYQVDPESRPMAMGPPPTPHFNVLADTPSGLVPLHLRSPQVPAAQQMLNFPEKNKEKPVDLQNFGLRTDIYSKKTLAKSKGASAGREWTEQETLLLLEALEMYKDDWNKVSEHVGSRTQDECILHFLRLPIEDPYLENSDASLGPLAYQPVPFSQSGNPVMSTVAFLASVVDPRVASAAAKAALEEFSRVREEVPLELVEAHVKKVQEAARASGKVDPTYGLESSCIAGTGPDEPEKLEGAEEEKMEADPDGQQPEKAENKVENETDEGDKAQDGENEKNSEKEQDSEVSEDTKSEEKETEENKELTDTCKERESDTGKKKVEHEISEGNVATAAAAALASAATKAKHLAAVEERKIKSLVALLVETQMKKLEIKLRHFEELETIMDREKEALEQQRQQLLTERQNFHMEQLKYAELRARQQMEQQQHGQNPQQAHQHSGGPGLAPLGAAGHPGMMPHQQPPPYPLMHHQMPPPHPPQPGQIPGPGSMMPGQHMPGRMIPTVAANIHPSGSGPTPPGMPPMPGNILGPRVPLTAPNGMYPPPPQQQPPPPPPADGVPPPPAPGPPASAAP");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(1064);
        Position end1 = new DefaultPosition(1064);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("R", "K");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);
        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Arg1064Lys", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_MultipleAminoAcidChangeNonSequentialPosition() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // can be tested with "EBI-9693147" and "EBI-15731927"
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //  IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-10889784",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation decreasing)");
        featureType.setMIIdentifier("MI:0119");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MSQEPEPGAMPYSPADDPSPLDLSLGSTSRRKKRKSHDIPNSPSKHPFPDGLSEEEKQKLERRRKRNRDAARRRRRKQTDYVDKLHEACEELQRANEHLRKEIRDLRTECTSLRVQLACHEPVCPMAVPLTVTLGLLTTPHDPVPEPPICTPPPPSPDEPNAPHCSGSQPPICTPPPPDTEELCAQLCSTPPPPISTPHIIYAPGPSPLQPPICTPPPPDAEELCAQLCSTPPPPICTPHSLFCPPQPPSPEGIFPALCPVTEPCTPPSPGTVYAQLCPVGQAPLFTPSPPHPAPEPERLYARLTEDPEQDSLYSGQIYIQFPSDTQSTVWWFPGDGRP");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(92);
        Position end1 = new DefaultPosition(92);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("L", "A");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);

        Position start2 = new DefaultPosition(99);
        Position end2 = new DefaultPosition(99);
        ResultingSequence resultingSequence2 = new DefaultResultingSequence("L", "A");
        Range range2 = new ExperimentalRange(start2, end2, resultingSequence2);

        Position start3 = new DefaultPosition(106);
        Position end3 = new DefaultPosition(106);
        ResultingSequence resultingSequence3 = new DefaultResultingSequence("L", "A");
        Range range3 = new ExperimentalRange(start3, end3, resultingSequence3);

        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.getRanges().add(range2);
        intactFeatureEvidence.getRanges().add(range3);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.[Leu92Ala;Leu99Ala;Leu106Ala]", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_MultipleAminoAcidChangeSequentialPosition_1() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // single AA change?
        // this above testFeature synced with following feature from database
        //  IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-11178974",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation decreasing)");
        featureType.setMIIdentifier("MI:0119");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MDNEEVNEECMRLFFKNARAHLDKHLTSRLTCDENAYITFRCFLDGIHRKSTRFLEELLLKQENMYHNNNYERINDSVIPLVLKLLWLQIHEPTLQWFEHWFHDIMRLSNRRKFRVFRIFQKKMIQFFKITHRYYYDIIEHLCAKYDMNSVISNALFAKLNLMQYTDGLSTHEKIILNTSNPLTFSIVISLQRCVINLGSTHFYKTLLNKPSNKPKSVEGFEKSIRYLNIASLYLPAVGDTYFQRAKIYLITGKFSLYFFELVRGALVRIPSKCALNNLKDFILTPDFPERRRLMKKLAILVSKDLKGEKSFFEGQIVLQFLSIVEHTLVPQSWNASRASNCWLLKEHLQMAALKYHSGNINVILENLAATMGSFDLMFTTRKSKEQKNKLKYADLSERQVFFLDLSFDFIANIIDVVIKPSWQKNMEDFRYLAIIRLLMCWIKSYRSILQYTHRHRKFCTSFALLLNDLINSPLNCSGNIYSHRPKRSYLFREDIIFREFSCINFALTDFNDDYVYDSPDMINNIIGCPTLTKVLSPKEECVLRIRSIIFSGMKFLEKNDTGVIWNASKYKFDLISPNIKIKRQIALSEISSKINVKTQQERVVSSRKVEAKRDEQQRKRAGKIAVTELEKQFANVRRTKKLSPLPEKDGVSSELVKHAASRGRKTITGPLSSDFLSYPDEAIDADEDITVQVPDTPT");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(465);
        Position end1 = new DefaultPosition(465);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("L", "G");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);

        Position start2 = new DefaultPosition(466);
        Position end2 = new DefaultPosition(466);
        ResultingSequence resultingSequence2 = new DefaultResultingSequence("L", "S");
        Range range2 = new ExperimentalRange(start2, end2, resultingSequence2);

        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.getRanges().add(range2);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.[Leu465Gly;Leu466Ser]", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_MultipleAminoAcidChangeSequentialPosition_2() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //  IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-8839684",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation disrupting)");
        featureType.setMIIdentifier("MI:0573");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MEGAGGANDKKKISSERRKEKSRDAARSRRSKESEVFYELAHQLPLPHNVSSHLDKASVMRLTISYLRVRKLLDAGDLDIEDDMKAQMNCFYLKALDGFVMVLTDDGDMIYISDNVNKYMGLTQFELTGHSVFDFTHPCDHEEMREMLTHRNGLVKKGKEQNTQRSFFLRMKCTLTSRGRTMNIKSATWKVLHCTGHIHVYDTNSNQPQCGYKKPPMTCLVLICEPIPHPSNIEIPLDSKTFLSRHSLDMKFSYCDERITELMGYEPEELLGRSIYEYYHALDSDHLTKTHHDMFTKGQVTTGQYRMLAKRGGYVWVETQATVIYNTKNSQPQCIVCVNYVVSGIIQHDLIFSLQQTECVLKPVESSDMKMTQLFTKVESEDTSSLFDKLKKEPDALTLLAPAAGDTIISLDFGSNDTETDDQQLEEVPLYNDVMLPSPNEKLQNINLAMSPLPTAETPKPLRSSADPALNQEVALKLEPNPESLELSFTMPQIQDQTPSPSDGSTRQSSPEPNSPSEYCFYVDSDMVNEFKLELVEKLFAEDTEAKNPFSTQDTDLDLEMLAPYIPMDDDFQLRSFDQLSPLESSSASPESASPQSTVTVFQQTQIQEPTANATTTTATTDELKTVTKDRMEDIKILIASPSPTHIHKETTSATSSPYRDTQSRTASPNRAGKGVIEQTEKSHPRSPNVLSVALSQRTTVPEEELNPKILALQNAQRKRKMEHDGSLFQAVGIGTLLQQPDDHAATTSLSWKRVKGCKSSEQNGMEQKTIILIPSDLACRLLGQSMDESGLPQLTSYDCEVNAPIQGSRNLLQGEELLRALDQVN");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(561);
        Position end1 = new DefaultPosition(562);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("ML", "AA");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);

        Position start2 = new DefaultPosition(564);
        Position end2 = new DefaultPosition(568);
        ResultingSequence resultingSequence2 = new DefaultResultingSequence("PYIPM", "AAAAA");
        Range range2 = new ExperimentalRange(start2, end2, resultingSequence2);

        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.getRanges().add(range2);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.[Met561_Leu562delinsAlaAla;Pro564_Met568delinsAlaAlaAlaAlaAla]", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_MultipleAminoAcidChangeSequentialPosition_3() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        // IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-2891626",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation decreasing)");
        featureType.setMIIdentifier("MI:0119");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MARTKQTARKSTGGKAPRKQLATKAARKSAPATGGVKKPHRYRPGTVALREIRRYQKSTELLIRKLPFQRLVREIAQDFKTDLRFQSSAVMALQEACEAYLVGLFEDTNLCAIHAKRVTIMPKDIQLARRIRGERA");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(2);
        Position end1 = new DefaultPosition(2);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("A", "AA");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);

        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Ala2[2]", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_MultipleAminoAcidChangeSequentialPosition_4() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // can also be tested with following
        //shortlabelGenerator.generateNewShortLabel("EBI-11314033");
        //shortlabelGenerator.generateNewShortLabel("EBI-12590047");
        //shortlabelGenerator.generateNewShortLabel("EBI-9846491");
        // shortlabelGenerator.generateNewShortLabel("EBI-5260097");
        // shortlabelGenerator.generateNewShortLabel("EBI-16880104");
        // shortlabelGenerator.generateNewShortLabel("EBI-16879936");
        // shortlabelGenerator.generateNewShortLabel("EBI-11475055");
        // shortlabelGenerator.generateNewShortLabel("EBI-5260343");
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-15582875",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation decreasing strength)");
        featureType.setMIIdentifier("MI:1133");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MNKAPQSTGPPPAPSPGLPQPAFPPGQTAPVVFSTPQATQMNTPSQPRQHFYPSRAQPPSSAASRVQSAAPARPGPAAHVYPAGSQVMMIPSQISYPASQGAYYIPGQGRSTYVVPTQQYPVQPGAPGFYPGASPTEFGTYAGAYYPAQGVQQFPTGVAPTPVLMNQPPQIAPKRERKTIRIRDPNQGGKDITEEIMSGARTASTPTPPQTGGGLEPQANGETPQVAVIVRPDDRSQGAIIADRPGLPGPEHSPSESQPSSPSPTPSPSPVLEPGSEPNLAVLSIPGDTMTTIQMSVEESTPISRETGEPYRLSPEPTPLAEPILEVEVTLSKPVPESEFSSSPLQAPTPLASHTVEIHEPNGMVPSEDLEPEVESSPELAPPPACPSESPVPIAPTAQPEELLNGAPSPPAVDLSPVSEPEEQAKEVTASMAPPTIPSATPATAPSATSPAQEEEMEEEEEEEEGEAGEAGEAESEKGGEELLPPESTPIPANLSQNLEAAAATQVAVSVPKRRRKIKELNKKEAVGDLLDAFKEANPAVPEVENQPPAGSNPGPESEGSGVPPRPEEADETWDSKEDKIHNAENIQPGEQKYEYKSDQWKPLNLEEKKRYDREFLLGFQFIFASMQKPEGLPHISDVVLDKANKTPLRPLDPTRLQGINCGPDFTPSFANLGRTTLSTRGPPRGGPGGELPRGPAGLGPRRSQQGPRKEPRKIIATVLMTEDIKLNKAEKAWKPSSKRTAADKDRGEEDADGSKTQDLFRRVRSILNKLTPQMFQQLMKQVTQLAIDTEERLKGVIDLIFEKAISEPNFSVAYANMCRCLMALKVPTTEKPTVTVNFRKLLLNRCQKEFEKDKDDDEVFEKKQKEMDEAATAEERGRLKEELEEARDIARRRSLGNIKFIGELFKLKMLTEAIMHDCVVKLLKNHDEESLECLCRLLTTIGKDLDFEKAKPRMDQYFNQMEKIIKEKKTSSRIRFMLQDVLDLRGSNWVPRRGDQGPKTIDQIHKEAEMEEHREHIKVQQLMAKGSDKRRGGPPGPPISRGLPLVDDGGWNTVPISKGSRPIDTSRLTKITKPGSIDSNNQLFAPGGRLSWGKGSSGGSGAKPSDAASEAARPATSTLNRFSALQQAVPTESTDNRRVVQRSSLSRERGEKAGDRGDRLERSERGGDRGDRLDRARTPATKRSFSKEVEERSRERPSQPEGLRKAASLTEDRDRGRDAVKREAALPPVSPLKAALSEEELEKKSKAIIEEYLHLNDMKEAVQCVQELASPSLLFIFVRHGVESTLERSAIAREHMGQLLHQLLCAGHLSTAQYYQGLYEILELAEDMEIDIPHVWLYLAELVTPILQEGGVPMGELFREITKPLRPLGKAASLLLEILGLLCKSMGPKKVGTLWREAGLSWKEFLPEGQDIGAFVAEQKVEYTLGEESEAPGQRALPSEELNRQLEKLLKEGSSNQRVFDWIEANLSEQQIVSNTLVRALMTAVCYSAIIFETPLRVDVAVLKARAKLLQKYLCDEQKELQALYALQALVVTLEQPPNLLRMFFDALYDEDVVKEDAFYSWESSKDPAEQQGKGVALKSVTAFFKWLREAEEESDHN");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(1408);
        Position end1 = new DefaultPosition(1412);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("PEGQD", "GAG..");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Pro1408_Asp1412delinsGlyAlaGly", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_MultipleAminoAcidChangeSequentialPosition_5() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // can also be tested with following
        //shortlabelGenerator.generateNewShortLabel("EBI-12687520");
        // shortlabelGenerator.generateNewShortLabel("EBI-11302770");
        //shortlabelGenerator.generateNewShortLabel("EBI-977116");
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        // IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-12687523",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation)");
        featureType.setMIIdentifier("MI:0118");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MAENLLDGPPNPKRAKLSSPGFSANDSTDFGSLFDLENDLPDELIPNGGELGLLNSGNLVPDAASKHKQLSELLRGGSSSSINPGIGNVSASSPVQQGLGGQAQGQPNSANMASLGAMGKSPLNQGDSSAPNLPKQAASTSGPTPPASQALNPQAQKQVGLVTSSPATSQTGPGICMNANFNQTHPGLLNSNSGHSLMNQAQQGQAQVMNGSLGAAGRGRGAGMPYPAPAMQGATSSVLAETLTQVSPQMASHAGLNTAQTGGMTKMGMAGNTSPFGQPFSQAGGQQMGAPGVNPQIPGKQSMVNSLPPFPADIKNASVTSVPNMSQMQTSVGIVPTQAIATGPTADPEKRKLIQQQLVLLLHAHKCQRREQANGEVRACSLPHCRTMKNVLNHMTHCQAGKACQVAHCASSRQIISHWKNCTRHDCPVCLPLKNASDKRNQQTILGSPASGIQNTIGSVGTGQQNATSLSNPNPIDPSSMQRAYAALGLPYLNQPQTQLQPQVPGQQPAQPQTHQQMRTLNPLGNNPMNIPAGGITTDQQPPSLISESALPTSLGATNPLMSDGATSGNIGPLSTLPSAAPPSSTGVRKGWHEHVTQDLRSHLVHKLVQAIFPTPDPAALKDRRMENLVAYAKKVEGDMYESANSRDEYYHLLAEKIYKIQKELEEKRRSRLHKQGILGNQPALPAPGTQPPGIPQAQPVRPPNGPMPLPVNRMQVSQGMNSFTPMSLGNVQLPQAPMGPRAASPMNHSVPMNSMGSVPGMAISPSRMPQPPNMMGTHANNMMAQAPAQNQFLPQNQFPSASGAMSVNSVGLGQPAAQAAVSQGQVPGAALPNPLNMLGPQASQLPCPPVTQSPLHQTPPPASTAAGLPSLQHPVAPGMTPPQPAAPTQPSTPVSSSGQTPTPTPGSVPSASQSQSTPTVQAAAQAQVTPQPQTPVQPPSVATPQSSQQQPTPVHTQPPGTPLSQAAASIDNRVPTPSSVASAETNSQQPGPDVPVLEMKAEVKTEDTEPDASEPKGEPGSGMMEEDLQGSSQVKEETDTTEQKSEPMEVDEKKPEVKVEAKEEEDGGANGAASQSTSPSQPRKKIFKPEELRQALMPTLEALYRQDPESLPFRQPVDPQLLGIPDYFDIVKNPMDLSTIKRKLDTGQYQEPWQYVDDVWLMFNNAWLYNRKTSRVYKFCSKLAEVFEQEIDPVMQSLGYCCGRKYEFSPQTLCCYGKQLCTIPRDAAYYSYQNRYHFCEKCFTEIQGENVTLGDDPSQPQTTISKDQFEKKKNDTLDPEPFVDCKECGRKMHQICVLHYDIIWPSGFVCDNCLKKTGRTRKENKFSAKRLQTTRLGNHLEDRVNKFLRRQNHPEAGEVFVRVVASSDKTVEVKPGMKSRFVDSGEMSESFPYRTKALFAFEEIDGVDVCFFGMHVQEYGSDCPPPNTRRVYISYLDSIHFFRPRCLRTAVYHEILIGYLEYVKKLGYVTGHIWACPPSEGDDYIFHCHPPDQKIPKPKRLQEWYKKMLDKAFAERIIHDYKDIFKQATEDRLTSAKELPYFEGDFWPNVLEESIKELEQEEEERKKEESTAASETTEGSQGDSKNAKKKNNKKTNKNKSSISRANKKKPSMPNVSNDLSQKLYATMEKHKEVFFVIHLHAGPVINTLPPIVDPDPLLSCDLMDGRDAFLTLARDKHWEFSSLRRSKWSTLCMLVELHTQGQDRFVYTCNECKHHVETRWHCTVCEDYDLCINCYNTKSHTHKMVKWGLGLDDEGSSQGEPQSKSPQESRRLSIQRCIQSLVHACQCRNANCSLPSCQKMKRVVQHTKGCKRKTNGGCPVCKQLIALCCYHAKHCQENKCPVPFCLNIKHKLRQQQIQHRLQQAQLMRRRMATMNTRNVPQQSLPSPTSAPPGTPTQQPSTPQTPQPPAQPQPSPVSMSPAGFPSVARTQPPTTVSAGKPTSQVPAPPPPAQPPPAAVEAARQIEREAQQQQHLYRVNINNGMPPGRTGMVTPGSQMAPVGLNVPRPNQVSGPVVPNLPPGQWQQAPIPQQQPMPGMPRPVMSMPAQPAVAGPRMPSVQPPRSISPGALQDLLRTLKSPSSPQQQQQVLNILKSNPQLMAAFIKQRTAKYVASQPGLQAQPSLQAQPGLQPQPGLHQQPGLQNLNAMQAGGPRPGVPPQQQTMGGLNPQGQALNIMNPGHSPSMASMNPQYREMLRRQLLQQQQQQQQQQQQQQGGAGMAGGMAGHGQFQQPQGPGGYPPAMQQQRMQQHLPIQGGSMGQMAAQMGQLGQMGQPGLGADSTPNIQQALQQRILQQQQMKQQIGSPGQPNPMSPQQHMLSGQPQASHLPGQQMATSLSSQVRSPAPVQSPRPQSQPPHSSPSPRIQPQPSPHHVSPQTGSPHPGLAVTMASSIDQGHLGNPEQSAMLPQLNTPNRSALSSELSLVGDTTGDTLEKFVEGL");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(2061);
        Position end1 = new DefaultPosition(2061);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("P", "SM");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Pro2061delinsSerMet", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_Deletion_1() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // can also be tested with following
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //  IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-6898602",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation)");
        featureType.setMIIdentifier("MI:0118");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MQRSPLEKASVVSKLFFSWTRPILRKGYRQRLELSDIYQIPSVDSADNLSEKLEREWDRELASKKNPKLINALRRCFFWRFMFYGIFLYLGEVTKAVQPLLLGRIIASYDPDNKEERSIAIYLGIGLCLLFIVRTLLLHPAIFGLHHIGMQMRIAMFSLIYKKTLKLSSRVLDKISIGQLVSLLSNNLNKFDEGLALAHFVWIAPLQVALLMGLIWELLQASAFCGLGFLIVLALFQAGLGRMMMKYRDQRAGKISERLVITSEMIENIQSVKAYCWEEAMEKMIENLRQTELKLTRKAAYVRYFNSSAFFFSGFFVVFLSVLPYALIKGIILRKIFTTISFCIVLRMAVTRQFPWAVQTWYDSLGAINKIQDFLQKQEYKTLEYNLTTTEVVMENVTAFWEEGFGELFEKAKQNNNNRKTSNGDDSLFFSNFSLLGTPVLKDINFKIERGQLLAVAGSTGAGKTSLLMVIMGELEPSEGKIKHSGRISFCSQFSWIMPGTIKENIIFGVSYDEYRYRSVIKACQLEEDISKFAEKDNIVLGEGGITLSGGQRARISLARAVYKDADLYLLDSPFGYLDVLTEKEIFESCVCKLMANKTRILVTSKMEHLKKADKILILHEGSSYFYGTFSELQNLQPDFSSKLMGCDSFDQFSAERRNSILTETLHRFSLEGDAPVSWTETKKQSFKQTGEFGEKRKNSILNPINSIRKFSIVQKTPLQMNGIEEDSDEPLERRLSLVPDSEQGEAILPRISVISTGPTLQARRRQSVLNLMTHSVNQGQNIHRKTTASTRKVSLAPQANLTELDIYSRRLSQETGLEISEEINEEDLKECFFDDMESIPAVTTWNTYLRYITVHKSLIFVLIWCLVIFLAEVAASLVVLWLLGNTPLQDKGNSTHSRNNSYAVIITSTSSYYVFYIYVGVADTLLAMGFFRGLPLVHTLITVSKILHHKMLHSVLQAPMSTLNTLKAGGILNRFSKDIAILDDLLPLTIFDFIQLLLIVIGAIAVVAVLQPYIFVATVPVIVAFIMLRAYFLQTSQQLKQLESEGRSPIFTHLVTSLKGLWTLRAFGRQPYFETLFHKALNLHTANWFLYLSTLRWFQMRIEMIFVIFFIAVTFISILTTGEGEGRVGIILTLAMNIMSTLQWAVNSSIDVDSLMRSVSRVFKFIDMPTEGKPTKSTKPYKNGQLSKVMIIENSHVKKDDIWPSGGQMTVKDLTAKYTEGGNAILENISFSISPGQRVGLLGRTGSGKSTLLSAFLRLLNTEGEIQIDGVSWDSITLQQWRKAFGVIPQKVFIFSGTFRKNLDPYEQWSDQEIWKVADEVGLRSVIEQFPGKLDFVLVDGGCVLSHGHKQLMCLARSVLSKAKILLLDEPSAHLDPVTYQIIRRTLKQAFADCTVILCEHRIEAMLECQQFLVIEENKVRQYDSIQKLLNERSLFRQAISPSDRVKLFPHRNSSKCKSKPQIAALKEETEEEVQDTRL");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(508);
        Position end1 = new DefaultPosition(508);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("F", ".");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Phe508del", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_Deletion_2() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // can also be tested with following
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-16008622",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation disrupting strength)");
        featureType.setMIIdentifier("MI:1128");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MGPKDSAKCLHRGPQPSHWAAGDGPTQERCGPRSLGSPVLGLDTCRAWDHVDGQILGQLRPLTEEEEEEGAGATLSRGPAFPGMGSEELRLASFYDWPLTAEVPPELLAAAGFFHTGHQDKVRCFFCYGGLQSWKRGDDPWTEHAKWFPSCQFLLRSKGRDFVHSVQETHSQLLGSWDPWEEPEDAAPVAPSVPASGYPELPTPRREVQSESAQEPGGVSPAEAQRAWWVLEPPGARDVEAQLRRLQEERTCKVCLDRAVSIVFVPCGHLVCAECAPGLQLCPICRAPVRSRVRTFLS");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(296);
        Position end1 = new DefaultPosition(298);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("FLS", "...");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Phe296_Ser298del", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_Deletion_3() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // can also be tested with following
        //shortlabelGenerator.generateNewShortLabel("EBI-1641252");
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //   IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-9085688",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation)");
        featureType.setMIIdentifier("MI:0118");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MNRCWALFLSLCCYLRLVSAEGDPIPEELYEMLSDHSIRSFDDLQRLLHGDPGEEDGAELDLNMTRSHSGGELESLARGRRSLGSLTIAEPAMIAECKTRTEVFEISRRLIDRTNANFLVWPPCVEVQRCSGCCNNRNVQCRPTQVQLRPVQVRKIEIVRKKPIFKKATVTLEDHLACKCETVAAARPVTRSPGGSQEQRAKTPQTRVTIRTVRVRRPPKGKHRKFKHTHDKTALKETLGA");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(108);
        Position end1 = new DefaultPosition(109);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("RR", "..");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Arg108_Arg109del", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_Insertion_1() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-2891626",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation decreasing)");
        featureType.setMIIdentifier("MI:0119");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MARTKQTARKSTGGKAPRKQLATKAARKSAPATGGVKKPHRYRPGTVALREIRRYQKSTELLIRKLPFQRLVREIAQDFKTDLRFQSSAVMALQEACEAYLVGLFEDTNLCAIHAKRVTIMPKDIQLARRIRGERA");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(2);
        Position end1 = new DefaultPosition(2);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("A", "AA");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Ala2[2]", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_Insertion_2() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        // IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-13639830",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation)");
        featureType.setMIIdentifier("MI:0118");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MAAASYDQLLKQVEALKMENSNLRQELEDNSNHLTKLETEASNMKEVLKQLQGSIEDEAMASSGQIDLLERLKELNLDSSNFPGVKLRSKMSLRSYGSREGSVSSRSGECSPVPMGSFPRRGFVNGSRESTGYLEELEKERSLLLADLDKEEKEKDWYYAQLQNLTKRIDSLPLTENFSLQTDMTRRQLEYEARQIRVAMEEQLGTCQDMEKRAQRRIARIQQIEKDILRIRQLLQSQATEAERSSQNKHETGSHDAERQNEGQGVGEINMATSGNGQGSTTRMDHETASVLSSSSTHSAPRRLTSHLGTKVEMVYSLLSMLGTHDKDDMSRTLLAMSSSQDSCISMRQSGCLPLLIQLLHGNDKDSVLLGNSRGSKEARARASAALHNIIHSQPDDKRGRREIRVLHLLEQIRAYCETCWEWQEAHEPGMDQDKNPMPAPVEHQICPAVCVLMKLSFDEEHRHAMNELGGLQAIAELLQVDCEMYGLTNDHYSITLRRYAGMALTNLTFGDVANKATLCSMKGCMRALVAQLKSESEDLQQVIASVLRNLSWRADVNSKKTLREVGSVKALMECALEVKKESTLKSVLSALWNLSAHCTENKADICAVDGALAFLVGTLTYRSQTNTLAIIESGGGILRNVSSLIATNEDHRQILRENNCLQTLLQHLKSHSLTIVSNACGTLWNLSARNPKDQEALWDMGAVSMLKNLIHSKHKMIAMGSAAALRNLMANRPAKYKDANIMSPGSSLPSLHVRKQKALEAELDAQHLSETFDNIDNLSPKASHRSKQRHKQSLYGDYVFDTNRHDDNRSDNFNTGNMTVLSPYLNTTVLPSSSSSRGSLDSSRSEKDRSLERERGIGLGNYHPATENPGTSSKRGLQISTTAAQIAKVMEEVSAIHTSQEDRSSGSTTELHCVTDERNALRRSSAAHTHSNTYNFTKSENSNRTCSMPYAKLEYKRSSNDSLNSVSSSDGYGKRGQMKPSIESYSEDDESKFCSYGQYPADLAHKIHSANHMDDNDGELDTPINYSLKYSDEQLNSGRQSPSQNERWARPKHIIEDEIKQSEQRQSRNQSTTYPVYTESTDDKHLKFQPHFGQQECVSPYRSRGANGSETNRVGSNHGINQNVSQSLCQEDDYEDDKPTNYSERYSEEEQHEEEERPTNYSIKYNEEKRHVDQPIDYSLKYATDIPSSQKQSFSFSKSSSGQSSKTEHMSSSSENTSTPSSNAKRQNQLHPSSAQSRSGQPQKAATCKVSSINQETIQTYCVEDTPICFSRCSSLSSLSSAEDEIGCNQTTQEADSANTLQIAEIKEKIGTRSAEDPVSEVPAVSQHPRTKSSRLQGSSLSSESARHKAVEFSSGAKSPSKSGAQTPKSPPEHYVQETPLMFSRCTSVSSLDSFESRSIASSVQSEPCSGMVSGIISPSDLPDSPGQTMPPSRSKTPPPPPQTAQTKREVPKNKAPTAEKRESGPKQAAVNAAVQRVQVLPDADTLLHFATESTPDGFSCSSSLSALSLDEPFIQKDVELRIMPPVQENDNGNETESEQPKESNENQEKEAEKTIDSEKDLLDDSDDDDIEILEECIISAMPTKSSRKAKKPAQTASKLPPPVARKPSQLPVYKLLPSQNRLQPQKHVSFTPGDDMPRVYCVEGTPINFSTATSLSDLTIESPPNELAAGEGVRGGAQSGEFEKRDTIPTEGRSTDEAQGGKTSSVTIPELDDNKAEEGDILAECINSAMPKGKSHKPFRVKKIMDQVQQASASSSAPNKNQLDGKKKKPTSPVKPIPQNTEYRTRVRKNADSKNNLNAERVFSDNKDSKKQNLKNNSKVFNDKLPNNEDRVRGSFAFDSPHHYTPIEGTPYCFSRNDSLSSLDFDDDDVDLSREKAELRKAKENKESEAKVTSHTELTSNQQSANKTQAIAKQPINRGQPKPILQKQSTFPQSSKDIPDRGAATDEKLQNFAIENTPVCFSHNSSLSSLSDIDQENNNKENEPIKETEPPDSQGEPSKPQASGYAPKSFHVEDTPVCFSRNSSLSSLSIDSEDDLLQECISSAMPKKKKPSRLKGDNEKHSPRNMGGILGEDLTLDLKDIQRPDSEHGLSPDSENFDWKAIQEGANSIVSSLHQAAAAACLSRQASSDSDSILSLKSGISLGSPFHLTPDQEEKPFTSNKGPRILKPGEKSTLETKKIESESKGIKGGKKVYKSLITGKVRSNSEISGQMKQPLQANMPSISRGRTMIHIPGVRNSSSSTSPVSKKGPPLKTPASKSPSEGQTATTSPRGAKPSVKSELSPVARQTSQIGGSSKAPSRSGSRDSTPSRPAQQPLSRPIQSPGRNSISPGRNGISPPNKLSQLPRTSSPSTASTKSSGSGKMSYTSPGRQMSQQNLTKQTGLSKNASSIPRSESASKGLNQMNNGNGANKKVELSRMSSTKSSGSESDRSERPVLVRQSTFIKEAPSPTLRRKLEESASFESLSPSSRPASPTRSQAQTPVLSPSLPDMSLSTHSSVQAGGWRKLPPNLSPTIEYNDGRPAKRHDIARSHSESPSRLPINRSGTWKREHSKHSSSLPRVSTWRRTGSSSSILSASSESSEKAKSEDEKHVNSISGTKQSKENQVSAKGTWRKIKENEFSPTNSTSQTVSSGATNGAESKTLIYQMAPAVSKTEDVWVRIEDCPINNPRSGRSPTGNTPPVIDSVSEKANPNIKDSKDNQAKQNVGNGSVPMRTVGLENRLNSFIQVDAPDQKGTEIKPGQNNPVPVSETNESSIVERTPFSSSSSSKHSSPSGTVAARVTPFNYNPSPRKSSADSTSARPSQIPTPVNNNTKKRDSKTDSTESSGTQSPKRHSGSYLVTSV");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(204);
        Position end1 = new DefaultPosition(205);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("LG", "LEEG");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Leu204_Gly205insGluGlu", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_Insertion_3() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-10761861",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation)");
        featureType.setMIIdentifier("MI:0118");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MRALPICLVALMLSGCSMLSRSPVEPVQSTAPQPKAEPAKPKAPRATPVRIYTNAEELVGKPFRDLGEVSGDSCQASNQDSPPSIPTARKRMQINASKMKANAVLLHSCEVTSGTPGCYRQAVCIGSALNITAK");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(116);
        Position end1 = new DefaultPosition(117);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("PG", "PXG");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Pro116_Gly117insXaa", intactFeatureEvidence.getShortName());
    }

    @Test
    public void shortlabelGeneratorTest_Deletion_Insertion() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        //IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-5260097",3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactProtein interactor = new IntactProtein("testInteractor");
        CvTerm featureType = new DefaultCvTerm("(mutation)");
        featureType.setMIIdentifier("MI:0118");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("protein");
        interactor.setInteractorType(interactorType);
        (interactor).setSequence("MNLIPTVIETTNRGERAYDIYSRLLKDRIIMLGSQIDDNVANSIVSQLLFLQAQDSEKDIYLYINSPGGSVTAGFAIYDTIQHIKPDVQTICIGMAASMGSFLLAAGAKGKRFALPNAEVMIHQPLGGAQGQATEIEIAANHILKTREKLNRILSERTGQSIEKIQKDTDRDNFLTAEEAKEYGLIDEVMVPETK");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(1);
        Position end1 = new DefaultPosition(1);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("M", "GSM");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.Met1delinsGlySerMet", intactFeatureEvidence.getShortName());
    }

    @Test
    @Ignore
    public void shortlabelGeneratorTest_ComplexAsInteractor() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        // IntactFeatureEvidence intactFeatureEvidence = new IntactFeatureEvidence();
        // this above testFeature synced with following feature from database
        IntactFeatureEvidence intactFeatureEvidence = shortlabelGenerator.getFeatureEvidence("EBI-16418349", 3);
        intactFeatureEvidence.setShortName("test11-22test");
        IntactComplex interactor = new IntactComplex("testComplex");
        CvTerm featureType = new DefaultCvTerm("(mutation decreasing)");
        featureType.setMIIdentifier("MI:0119");
        intactFeatureEvidence.setType(featureType);
        CvTerm interactorType = new DefaultCvTerm("stable complex");
        interactor.setInteractorType(interactorType);
        //  (interactor).setSequence("MNLIPTVIETTNRGERAYDIYSRLLKDRIIMLGSQIDDNVANSIVSQLLFLQAQDSEKDIYLYINSPGGSVTAGFAIYDTIQHIKPDVQTICIGMAASMGSFLLAAGAKGKRFALPNAEVMIHQPLGGAQGQATEIEIAANHILKTREKLNRILSERTGQSIEKIQKDTDRDNFLTAEEAKEYGLIDEVMVPETK");
        ParticipantEvidence participant = new DefaultParticipantEvidence(interactor);
        Position start1 = new DefaultPosition(63);
        Position end1 = new DefaultPosition(63);
        ResultingSequence resultingSequence1 = new DefaultResultingSequence("I", "K");
        Range range1 = new ExperimentalRange(start1, end1, resultingSequence1);

        Position start2 = new DefaultPosition(66);
        Position end2 = new DefaultPosition(66);
        ResultingSequence resultingSequence2 = new DefaultResultingSequence("L", "K");
        Range range2 = new ExperimentalRange(start2, end2, resultingSequence2);


        intactFeatureEvidence.getRanges().clear();
        intactFeatureEvidence.getRanges().add(range1);
        intactFeatureEvidence.getRanges().add(range2);
        intactFeatureEvidence.setParticipant(participant);
        shortlabelGenerator.generateNewShortLabel(intactFeatureEvidence);
        Assert.assertEquals("p.[Ile63Lys;Leu66Lys]", intactFeatureEvidence.getShortName());
    }


    @Test
    @Ignore
    public void shortlabelGeneratorTest_1() {
        ShortlabelGenerator shortlabelGenerator = getShortlabelGenerator();
        shortlabelGenerator.addListener(new FeatureListener());

        // More cases

        //shortlabelGenerator.generateNewShortLabel("EBI-16022338");
        //shortlabelGenerator.generateNewShortLabel("EBI-16007653");
        // shortlabelGenerator.generateNewShortLabel("EBI-15938869");
        // shortlabelGenerator.generateNewShortLabel("EBI-13639830");
        //shortlabelGenerator.generateNewShortLabel("EBI-6556225");


        //complex as an interactor
        //  shortlabelGenerator.generateNewShortLabel("EBI-16418349");

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
        Double remainder = (double) (rSequence.length() % oSequence.length());
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
        boolean isInsertion = false;
        String oSequence = "LL";
        String rSequence = "GGSGL";
        String pattern = ".*";
        char[] originalSeqArray = oSequence.toCharArray();
        for (char oSeqChar : originalSeqArray) {
            pattern = pattern + "(?=.*" + oSeqChar + ")";
        }
        pattern = pattern + ".*";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(rSequence);
        boolean matched = m.matches();
        int count = 0;
        if (matched) {
            while (m.find()) {
                count++;

            }
        }

        if (count == oSequence.length()) {
            isInsertion = true;
        }


    }


}
