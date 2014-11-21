package vu.wntools.wordnet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import vu.wntools.util.Pos;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordnetLmfSaxParser extends DefaultHandler {

    public WordnetData wordnetData;
    private String value = "";
    private String sourceId = "";
    private String targetId= "";
    private String type = "";
    private String entry = "";
    private String pos = "";
    private String posFilter = "";
    private boolean posMatch = true;
    private ArrayList<String> relations = new ArrayList<String>();
    private ArrayList<String> synsets = new ArrayList<String>();
    private ArrayList<String> hypers = new ArrayList<String>();
    private ArrayList<String> others = new ArrayList<String>();
    private ArrayList<String> directequivalences = new ArrayList<String>();
    private ArrayList<String> nearequivalences = new ArrayList<String>();
    private ArrayList<String> otherequivalences = new ArrayList<String>();

    /**
     *       <Synset id="nld-21-d_n-11517-n">
     <SynsetRelations>
     <SynsetRelation target="nld-21-d_n-10345-n" relType="HAS_HYPERONYM">
     <Meta author="Paul" date="19961206" source="d_n-11517" confidence="0"/>
     </SynsetRelation>
     <SynsetRelation target="nld-21-d_n-40818-n" relType="HAS_MERO_PART">
     <Meta author="Laura" date="1998026" source="d_n-11517" confidence="0"/>
     </SynsetRelation>
     </SynsetRelations>
     <MonolingualExternalRefs>
     <MonolingualExternalRef externalSystem="pwn-20"
     externalReference="eng-20-05247300-n"
     relType="EQ_NEAR_SYNONYM">
     <Meta author="Irion Technologies"
     date="20070622"
     source="Irion Wordnet Aligner 1.0"
     confidence="43"/>
     </MonolingualExternalRef>
     <MonolingualExternalRef externalSystem="pwn-30"
     externalReference="eng-30-05565696-n"
     relType="EQ_NEAR_SYNONYM">
     <Meta author="Irion Technologies"
     date="20070622"
     source="Irion Wordnet Aligner 1.0"
     confidence="43"/>
     </MonolingualExternalRef>

     <MonolingualExternalRef externalSystem="wordnet_domain" externalReference="anatomy"/>
     </MonolingualExternalRefs>
     </Synset>
     */


    public WordnetLmfSaxParser() {
        wordnetData = new WordnetData();
    }

    public ArrayList<String> getRelations() {
        return relations;
    }

    public void setRelations(ArrayList<String> relations) {
        this.relations = relations;
    }


    public void setPos (String pos) {
        posFilter = pos;
        posMatch = false;
    }

    public ArrayList<String> getDirectEquivalences() {
        return directequivalences;
    }

    public void setDirectequivalences(ArrayList<String> equivalences) {
        this.directequivalences = equivalences;
    }

    public ArrayList<String> getNearEquivalences() {
        return nearequivalences;
    }

    public void setNearEquivalences(ArrayList<String> nearequivalences) {
        this.nearequivalences = nearequivalences;
    }

    public ArrayList<String> getOtherEquivalences() {
        return otherequivalences;
    }

    public void setOtherEquivalences(ArrayList<String> otherequivalences) {
        this.otherequivalences = otherequivalences;
    }

    public void parseFile(String filePath) {
        //System.out.println("filePath = " + filePath);
        if (!(new File(filePath)).exists()) {
            System.out.println("Cannot find file");
            return;
        }
        String myerror = "";
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            InputSource inp = new InputSource (new FileReader(filePath));
            parser.parse(inp, this);
        } catch (SAXParseException err) {
            myerror = "\n** Parsing error" + ", line " + err.getLineNumber()
                    + ", uri " + err.getSystemId();
            myerror += "\n" + err.getMessage();
            System.out.println("myerror = " + myerror);
        } catch (SAXException e) {
            Exception x = e;
            if (e.getException() != null)
                x = e.getException();
            myerror += "\nSAXException --" + x.getMessage();
            System.out.println("myerror = " + myerror);
        } catch (Exception eee) {
            eee.printStackTrace();
            myerror += "\nException --" + eee.getMessage();
            System.out.println("myerror = " + myerror);
        }
      //  System.out.println("myerror = " + myerror);
    }//--c


    /*
    <LexicalEntry id="clean_out">
    <Lemma partOfSpeech="v" writtenForm="clean_out" />
<Sense id="clean_out_1" synset="eng-30-00448864-v"><MonolingualExternalRefs><MonolingualExternalRef externalReference="clean_out%2:30:00::" externalSystem="Wordnet3.0" /></MonolingualExternalRefs></Sense>
<Sense id="clean_out_3" synset="eng-30-02314784-v"><MonolingualExternalRefs><MonolingualExternalRef externalReference="clean_out%2:40:00::" externalSystem="Wordnet3.0" /></MonolingualExternalRefs></Sense>
<Sense id="clean_out_2" synset="eng-30-02403408-v"><MonolingualExternalRefs><MonolingualExternalRef externalReference="clean_out%2:41:00::" externalSystem="Wordnet3.0" /></MonolingualExternalRefs></Sense>
</LexicalEntry>

    <Synset baseConcept="1"
    id="eng-30-00194834-r">
    <Definition gloss="in a bewildered manner" />
    <SynsetRelations>
    <SynsetRelation relType="related_to"
    target="eng-30-00000000-n" /></SynsetRelations>
    <MonolingualExternalRefs>
    <MonolingualExternalRef externalReference="dummy" externalSystem="Domain" />
    </MonolingualExternalRefs></Synset>
     */
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {

        //<Lexicon label="Princeton WordNet 3.0" language="eng" languageCoding="ISO 639-3" owner="KYOTO project" version="3.0">

        if (qName.equalsIgnoreCase("Lexicon")) {

            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("label")) {
                    wordnetData.setResource(attributes.getValue(i).trim());
                } else if (attributes.getQName(i).equalsIgnoreCase("version")) {
                    wordnetData.setVersion(attributes.getValue(i).trim());
                }
            }
        }
        else if (qName.equalsIgnoreCase("LexicalEntry")) {
            entry = "";
            pos = "";
            synsets = new ArrayList<String>();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("writtenForm")) {
                    entry = attributes.getValue(i).trim();
                }
                else if (attributes.getQName(i).equalsIgnoreCase("partOfSpeech")) {
                    pos = attributes.getValue(i).trim();
                    pos = Pos.convertToShortPos(pos);

                }
            }
        }
        else if (qName.equalsIgnoreCase("Lemma")) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("writtenForm")) {
                    entry = attributes.getValue(i).trim();
                }
                else if (attributes.getQName(i).equalsIgnoreCase("partOfSpeech")) {
                    pos = attributes.getValue(i).trim();
                    pos = Pos.convertToShortPos(pos);

                }
            }
        }
        else if (qName.equalsIgnoreCase("Sense")) {
            String synsetId = "";
            String lexicalUnitId = "";
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("synset")) {
                    synsetId = attributes.getValue(i).trim();
                    synsets.add(synsetId);
                }
                if (attributes.getQName(i).equalsIgnoreCase("senseId")) {
                    lexicalUnitId = attributes.getValue(i).trim();
                }
            }
            if (!synsetId.isEmpty() && !lexicalUnitId.isEmpty()) {
                if (wordnetData.synsetToLexicalUnits.containsKey(synsetId)) {
                   ArrayList<String> lus = wordnetData.synsetToLexicalUnits.get(synsetId);
                   if (!lus.contains(lexicalUnitId)) {
                       lus.add(lexicalUnitId);
                       wordnetData.synsetToLexicalUnits.put(synsetId, lus);
                   }
                }
                else {
                    ArrayList<String> lus = new ArrayList<String>();
                    lus.add(lexicalUnitId);
                    wordnetData.synsetToLexicalUnits.put(synsetId, lus);
                }
            }
        }
        else if (qName.equalsIgnoreCase("Synset")) {
            sourceId = "";
            others = new ArrayList<String>();
            hypers = new ArrayList<String>();
            directequivalences = new ArrayList<String>();
            nearequivalences = new ArrayList<String>();
            otherequivalences = new ArrayList<String>();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("id")) {
                    sourceId = attributes.getValue(i).trim();
                }
            }
        }
        else if (qName.equalsIgnoreCase("SynsetRelation")) {
            type = "";
            targetId = "";
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("target")) {
                    targetId = attributes.getValue(i).trim();
                }
                else if (attributes.getQName(i).equalsIgnoreCase("relType")) {
                    type = attributes.getValue(i).trim();
                }

            }

            if (relations.size()==0) {
                if (type.equalsIgnoreCase("hypernym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("has_hypernym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("has_hyperonym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("near_synonym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("eng_derivative")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
                else if (type.equalsIgnoreCase("xpos_near_synonym")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
                else if (type.equalsIgnoreCase("event")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
                else if (type.equalsIgnoreCase("xpos_near_hyperonym")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
                else if (type.equalsIgnoreCase("xpos_near_hypernym")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
            }
            else if (relations.contains(type)) {
                if (!targetId.isEmpty()) hypers.add(targetId);
            }
            else {
                if (!targetId.isEmpty()) others.add(targetId);
            }

        }
        else if (qName.equalsIgnoreCase("SynsetRelation")) {
            type = "";
            targetId = "";
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("target")) {
                    targetId = attributes.getValue(i).trim();
                }
                else if (attributes.getQName(i).equalsIgnoreCase("relType")) {
                    type = attributes.getValue(i).trim();
                }

            }

            if (relations.size()==0) {
                if (type.equalsIgnoreCase("hypernym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("has_hypernym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("has_hyperonym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("near_synonym")) {
                    if (!targetId.isEmpty()) hypers.add(targetId);
                }
                else if (type.equalsIgnoreCase("eng_derivative")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
                else if (type.equalsIgnoreCase("xpos_near_synonym")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
                else if (type.equalsIgnoreCase("xpos_near_hyperonym")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
                else if (type.equalsIgnoreCase("xpos_near_hypernym")) {
                    if (!targetId.isEmpty()) others.add(targetId);
                }
            }
            else if (relations.contains(type)) {
                if (!targetId.isEmpty()) hypers.add(targetId);
            }
            else {
                if (!targetId.isEmpty()) others.add(targetId);
            }

        }
        else if (qName.equalsIgnoreCase("MonolingualExternalRef")) {

            /*            <MonolingualExternalRef externalSystem="pwn-20"
            externalReference="eng-20-05247300-n"
            relType="EQ_NEAR_SYNONYM">
            <Meta author="Irion Technologies"
            date="20070622"
            source="Irion Wordnet Aligner 1.0"
            confidence="43"/>
            </MonolingualExternalRef>*/
            type = "";
            targetId = "";
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("externalReference")) {
                    targetId = attributes.getValue(i).trim();
                }
                else if (attributes.getQName(i).equalsIgnoreCase("relType")) {
                    type = attributes.getValue(i).trim();
                }

            }

            if (type.equalsIgnoreCase("eq_synonym")) {
                    if (!targetId.isEmpty()) directequivalences.add(targetId);
            }
            else if (type.equalsIgnoreCase("eq_near_synonym")) {
                if (!targetId.isEmpty()) nearequivalences.add(targetId);
            }
            else if (type.toLowerCase().startsWith("eq_")) {
                if (!targetId.isEmpty()) otherequivalences.add(targetId);
            }

        }
        else {
        }
        value = "";

    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {


        if (qName.equalsIgnoreCase("Synset")) {
            if ((posFilter.isEmpty()) || sourceId.endsWith(posFilter)) {
                /// we store all synsets that we find and build the full graph
               // System.out.println("posFilter = " + posFilter);
               // System.out.println("sourceId = " + sourceId);
                if ((!sourceId.isEmpty()) && hypers.size()>0) {
                    wordnetData.addHyperRelation(sourceId, hypers);
                }
                if ((!sourceId.isEmpty()) && others.size()>0) {
                    wordnetData.addOtherRelations(sourceId, others);
                }
                if ((!sourceId.isEmpty()) && directequivalences.size()>0) {
                    wordnetData.addSynsetToDirectEquiSynsets(sourceId, directequivalences);
                }
                if ((!sourceId.isEmpty()) && nearequivalences.size()>0) {
                    wordnetData.addSynsetToNearEquiSynsets(sourceId, nearequivalences);
                }
                if ((!sourceId.isEmpty()) && otherequivalences.size()>0) {
                    wordnetData.addSynsetToOtherEquiSynsets(sourceId, otherequivalences);
                }
                sourceId = "";
                others = new ArrayList<String>();
                hypers = new ArrayList<String>();
                directequivalences = new ArrayList<String>();
                nearequivalences = new ArrayList<String>();
                otherequivalences = new ArrayList<String>();


    /*            if (wordnetData.getHyperRelations().size()%1000==0) {
                    System.out.println("wordnetData.getHyperRelations().size() = " + wordnetData.getHyperRelations().size());
                }*/
            }
            else {
                if (!pos.equalsIgnoreCase("n")) {
                  //  System.out.println("pos = " + pos);
                }
            }
        }
        else if (qName.equalsIgnoreCase("LexicalEntry")) {
            if (!entry.isEmpty()) {
                if ((posFilter.isEmpty()) || pos.isEmpty() || (pos.equalsIgnoreCase(posFilter))) {
                    //System.out.println("pos = " + pos);
                    //System.out.println("posFilter = " + posFilter);

                    if (wordnetData.entryToSynsets.containsKey(entry)) {
                        ArrayList<String> storedSynsets = wordnetData.entryToSynsets.get(entry);
                        for (int i = 0; i < synsets.size(); i++) {
                            String s = synsets.get(i);
                            if (!storedSynsets.contains(s)) {
                                storedSynsets.add(s);
                            }
                        }
                       // System.out.println("storedSynsets = " + storedSynsets);
                        wordnetData.entryToSynsets.put(entry, storedSynsets);
                    }
                    else {
                        wordnetData.entryToSynsets.put(entry, synsets);
                    }
                }
                else {
                  //  System.out.println("pos = " + pos);
                }
            }

/*
            if (wordnetData.entryToSynsets.size()%10000==0) {
                System.out.println("wordnetData.entryToSynsets.size() = " + wordnetData.entryToSynsets.size());
                //System.out.println("synsets = " + synsets.size());
            }
*/
            entry = "";
            synsets = new ArrayList<String>();
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }

    static public void main (String[] args) {
        //String pathToFile = args[0];
       // String pathToFile = "/Releases/wordnetsimilarity_v.0.1/resources/cornetto2.0.lmf.xml";
        String pathToFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        ArrayList<String> relations = new ArrayList<String>();
        //relations.add("NEAR_SYNONYM");
        //relations.add("HAS_HYPERONYM");
        //relations.add("HAS_MERO_PART");
        //relations.add("HAS_HOLO_PART");

        WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
        //parser.setPos("v");
        //parser.setRelations(relations);

        parser.parseFile(pathToFile);
/*
        int depth = parser.wordnetData.getAverageDepthByWord();
        System.out.println("depth = " + depth);
*/
        parser.wordnetData.buildSynsetIndex();
        if (parser.wordnetData.entryToSynsets.containsKey("person")) {
            System.out.println("HAS IT");
        }
            System.out.println("parser.wordnetData.entryToSynsets.size() = " + parser.wordnetData.entryToSynsets.size());
        System.out.println("parser.wordnetData.getHyperRelations().size() = " + parser.wordnetData.getHyperRelations().size());
        System.out.println("parser.wordnetData.getOtherRelations().size() = " + parser.wordnetData.getOtherRelations().size());

    }
}
