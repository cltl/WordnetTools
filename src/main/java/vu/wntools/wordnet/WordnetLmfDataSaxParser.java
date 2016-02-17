package vu.wntools.wordnet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import vu.wntools.lmf.*;
import vu.wntools.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordnetLmfDataSaxParser extends DefaultHandler {

    public WordnetLmfData wordnetData;
    private String value = "";
    private LmfEntry lmfEntry;
    private Synset synset;
    private SynsetRelation synsetRelation;
    private LmfSense lmfSense;

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


    public WordnetLmfDataSaxParser() {
        wordnetData = new WordnetLmfData();
    }


    public void parseFile(String filePath) {
        //System.out.println("filePath = " + filePath);
        if (!(new File(filePath)).exists()) {
            System.out.println("Cannot find file");
            return;
        }
        String myerror = "";
        try {
            InputStream fis = new FileInputStream(filePath);

            if (filePath.toLowerCase().endsWith(".gz")) {
                InputStream fileStream = new FileInputStream(filePath);
                fis = new GZIPInputStream(fileStream);
            }
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
           // InputSource inp = new InputSource (new FileReader(filePath));
            InputSource inp = new InputSource (fis);
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
                    wordnetData.setLexiconLabel(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("languageEncoding")) {
                    wordnetData.setLanguageEncoding(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("language")) {
                    wordnetData.setLanguage(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("owner")) {
                    wordnetData.setOwner(attributes.getValue(i).trim());
                }
            }
        }
        else if (qName.equalsIgnoreCase("GlobalInformation")) {

            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("label")) {
                    wordnetData.setGlobalLabel(attributes.getValue(i).trim());
                }
            }
        }
        else if (qName.equalsIgnoreCase("LexicalEntry")) {
            lmfEntry = new LmfEntry();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("id")) {
                    lmfEntry.setEntryId(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("partOfSpeech")) {
                    lmfEntry.setPos(attributes.getValue(i).trim());
                }
            }

            /**
             * /**
             * <LexicalEntry id="clapboard">
             *     <Lemma partOfSpeech="v" writtenForm="clapboard" />
             <Sense id="clapboard_1"
             synset="eng-30-01337412-v">
             <MonolingualExternalRefs>
             <MonolingualExternalRef externalReference="clapboard%2:35:00::" externalSystem="Wordnet3.0" />
             </MonolingualExternalRefs>
             </Sense>
             </LexicalEntry>
             */
        /*
         <LexicalEntry id="ANWB-kantoor-n-1" partOfSpeech="noun">
      <Lemma writtenForm="ANWB-kantoor"/>
      <WordForms>
        <WordForm writtenForm="ANWB-kantoor" grammaticalNumber="singular" article="het"/>
        <WordForm writtenForm="ANWB-kantoren" grammaticalNumber="plural" article="de"/>
      </WordForms>
      <Morphology morphoType="compound"/>
      <MorphoSyntax pronominalAndGrammaticalGender="m_f"/>
      <Sense senseId="r_n-3696" definition="kantoor v.d. ANWB" synset="odwn-10-107184172-n" origin="cdb2.2_None">
        <SenseRelations/>
        <Semantics-noun reference="common" countability="count" semanticType="artefact">
          <semanticShifts-noun semanticType="artefact"/>
          <semanticShifts-noun semanticType="place"/>
        </Semantics-noun>
        <Pragmatics>
          <Domains domain="transport"/>
        </Pragmatics>
      </Sense>
    </LexicalEntry>
         */
        }
        else if (qName.equalsIgnoreCase("Lemma")) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("writtenForm")) {
                    lmfEntry.setWrittenForm(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("partOfSpeech")) {
                    lmfEntry.setPos(attributes.getValue(i).trim());
                }
            }
        }
        else if (qName.equalsIgnoreCase("Sense")) {
            lmfSense = new LmfSense();
/*
    <Sense id="clapboard_1" synset="eng-30-01337412-v"/>
*/
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("id")) {
                    lmfSense.setSenseId(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("senseId")) {
                    lmfSense.setSenseId(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("origin")) {
                    lmfSense.setProvenance(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("provenance")) {
                    lmfSense.setProvenance(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("synset")) {
                    String synsetId = attributes.getValue(i).trim();
                    lmfSense.setSynset(synsetId);
                    if (wordnetData.synsetToEntriesMap.containsKey(synsetId)) {
                        ArrayList<String> entries = wordnetData.synsetToEntriesMap.get(synsetId);
                        if (!entries.contains(lmfEntry.getWrittenForm())) {
                            entries.add(lmfEntry.getWrittenForm());
                            wordnetData.synsetToEntriesMap.put(synsetId, entries);
                        }
                    }
                    else {
                        ArrayList<String> entries = new ArrayList<String>();
                        entries.add(lmfEntry.getWrittenForm());
                        wordnetData.synsetToEntriesMap.put(synsetId, entries);
                    }
                    if (wordnetData.entryToSynsetsMap.containsKey(lmfEntry.getWrittenForm())) {
                        ArrayList<String> synsets = wordnetData.entryToSynsetsMap.get(lmfEntry.getWrittenForm());
                        if (!synsets.contains(synsetId)) {
                            synsets.add(synsetId);
                            wordnetData.entryToSynsetsMap.put(lmfEntry.getWrittenForm(), synsets);
                        }
                    }
                    else {
                        ArrayList<String> synsets = new ArrayList<String>();
                        synsets.add(synsetId);
                        wordnetData.entryToSynsetsMap.put(lmfEntry.getWrittenForm(), synsets);
                    }
                }
                else if (attributes.getQName(i).equalsIgnoreCase("definition")) {
                    lmfSense.setDefinition(attributes.getValue(i).trim());
                }
            }
            lmfEntry.addSense(lmfSense);
        }
        else if (qName.equalsIgnoreCase("Synset")) {
            synset = new Synset();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("id")) {
                    synset.setSynsetId(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("ili")) {
                    synset.setIliId(attributes.getValue(i).trim());
                }
            }
            /**
                 <Synset id="mwn-569856-n" ili=""> <!-- PROPOSING A NEW ILI -->
                    <Definition language= “en” gloss="cultural habit to walk on skates"/>
                 </Synset>
            **/
        }
        else if (qName.equalsIgnoreCase("SynsetRelation")) {
            synsetRelation = new SynsetRelation();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("target")) {
                    synsetRelation.setTarget(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("relType")) {
                    synsetRelation.setRelType(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("provenance")) {
                    synsetRelation.setProvenance(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("source")) {
                    synsetRelation.setProvenance(attributes.getValue(i).trim());
                }
            }
            synset.getRelations().add(synsetRelation);

           // synset.addRelations(synsetRelation);
        }
        else if (qName.equalsIgnoreCase("Definition")) {
            Gloss definition = new Gloss();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("language")) {
                    definition.setLanguage(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("gloss")) {
                    definition.setText(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("provenance")) {
                    definition.setProvenance(attributes.getValue(i).trim());
                }
            }
            synset.addDefinition(definition);
        }
        else {
        }
        value = "";

    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {


        /**
         * <Synset id="eng-30-01155090-v" ili="i27400">
         <Definitions>
         <Definition gloss="gelijk komen" language="nl" provenance="odwn"/>
         <Definition gloss="catch up with and possibly overtake" language="en" provenance="pwn"/>
         <Definition gloss="get equal" language="en" provenance="google-translate"/>
         </Definitions>
         </Synset>
         */
        if (qName.equalsIgnoreCase("Synset")) {
            if (synset.getSynsetId().equals("eng-30-10980256-n")) {
                System.out.println("synset.toString() = " + synset.toString());
            }
            else if (synset.getSynsetId().equals("eng-30-01155090-v")) {
                System.out.println("synset = " + synset.toString());
            }
            if (wordnetData.synsetMap.containsKey(synset.getSynsetId())) {
                Synset storedSynset = wordnetData.synsetMap.get(synset.getSynsetId());
                storedSynset.merge(synset);
                wordnetData.synsetMap.put(storedSynset.getSynsetId(), storedSynset);
            }
            else {
                wordnetData.synsetMap.put(synset.getSynsetId(), synset);
                // wordnetData.synsets.add(synset);
            }
            /*if (wordnetData.addSynset(synset)) {
                /// if already there the info is merged and false is returned
                /// otherwise it is added and we update the synsetMap.
                wordnetData.synsetMap.put(synset.getSynsetId(), synset);
            }*/

        }




        /*
        <Synset id="eng-30-10980256-n">
      <SynsetRelations>
        <SynsetRelation target="eng-30-10181137-n" relType="instance" source="pwn"/>
        <SynsetRelation target="eng-30-10181137-n" relType="has_hyperonym" source="pwn"/>
      </SynsetRelations>
      <MonolingualExternalRefs/>
    </Synset>
         */
/*        else if (qName.equalsIgnoreCase("LexicalEntry")) {
            wordnetData.addEntry(lmfEntry);
            String writtenForm = lmfEntry.getWrittenForm();
            if (writtenForm.isEmpty()) {
                writtenForm = "empty";
            }
            if (wordnetData.entryMap.containsKey(writtenForm)) {
                ArrayList<LmfEntry> lmfEntries = wordnetData.entryMap.get(writtenForm);
                lmfEntries.add(lmfEntry);
                wordnetData.entryMap.put(writtenForm,lmfEntries);
            }
            else {
                ArrayList<LmfEntry> lmfEntries = new ArrayList<LmfEntry>();
                lmfEntries.add(lmfEntry);
                wordnetData.entryMap.put(writtenForm,lmfEntries);
            }
        }*/
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }

    static public void main (String[] args) {
        try {
            String glossLanguage = "nl";
            String glossOwner = "odwn";
            String pathToLmfFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf";
            String pathToGlossFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf.source-gloss";
            String pathToIliFile  = "/Users/piek/Desktop/GWG/ili.ttl";
            ReadILI readILI = new ReadILI();
            readILI.readILIFile(pathToIliFile);
            System.out.println("readILI.synsetToILIMap.size() = " + readILI.synsetToILIMap.size());
            ReadGlosses readGlosses = new ReadGlosses();
            readGlosses.readGlossFile(pathToGlossFile, glossLanguage, glossOwner);
            System.out.println("readGlosses.synsetToGlosses.size() = " + readGlosses.synsetToGlosses.size());
            WordnetLmfDataSaxParser wordnetLmfDataSaxParser = new WordnetLmfDataSaxParser();
            wordnetLmfDataSaxParser.parseFile(pathToLmfFile);

            for (int i = 0; i < wordnetLmfDataSaxParser.wordnetData.getSynsets().size(); i++) {
                Synset synset = wordnetLmfDataSaxParser.wordnetData.getSynsets().get(i);
                String synsetID = synset.getSynsetId();
                if (synsetID.startsWith("eng-30-")) {
                    synsetID = "eng-"+synsetID.substring(7);
                }
               // System.out.println("synsetID = " + synsetID);
                if (readILI.synsetToILIMap.containsKey(synsetID)) {
                    String iliId = readILI.synsetToILIMap.get(synsetID);
                    synset.setIliId(iliId);
                }
                if (readGlosses.synsetToGlosses.containsKey(synset.getSynsetId())) {
                    ArrayList<Gloss> defs = readGlosses.synsetToGlosses.get(synset.getSynsetId());
                    for (int j = 0; j < defs.size(); j++) {
                        Gloss def = defs.get(j);
                        synset.addDefinition(def);
                    }
                }
            }

            OutputStream fos = new FileOutputStream(pathToLmfFile+".test");
            wordnetLmfDataSaxParser.wordnetData.serialize(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static public void main_org (String[] args) {
        try {
            String glossLanguage = "nl";
            String glossOwner = "odwn";
            String pathToLmfFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf";
            String pathToGlossFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf.source-gloss";
            String pathToIliFile  = "/Users/piek/Desktop/GWG/ili.ttl";
            ReadILI readILI = new ReadILI();
            readILI.readILIFile(pathToIliFile);
            System.out.println("readILI.synsetToILIMap.size() = " + readILI.synsetToILIMap.size());
            ReadGlosses readGlosses = new ReadGlosses();
            readGlosses.readGlossFile(pathToGlossFile, glossLanguage, glossOwner);
            System.out.println("readGlosses.synsetToGlosses.size() = " + readGlosses.synsetToGlosses.size());
            WordnetLmfDataSaxParser wordnetLmfDataSaxParser = new WordnetLmfDataSaxParser();
            wordnetLmfDataSaxParser.parseFile(pathToLmfFile);

            for (int i = 0; i < wordnetLmfDataSaxParser.wordnetData.getSynsets().size(); i++) {
                Synset synset = wordnetLmfDataSaxParser.wordnetData.getSynsets().get(i);
                String synsetID = synset.getSynsetId();
                if (synsetID.startsWith("eng-30-")) {
                    synsetID = "eng-"+synsetID.substring(7);
                }
               // System.out.println("synsetID = " + synsetID);
                if (readILI.synsetToILIMap.containsKey(synsetID)) {
                    String iliId = readILI.synsetToILIMap.get(synsetID);
                    synset.setIliId(iliId);
                }
                if (readGlosses.synsetToGlosses.containsKey(synset.getSynsetId())) {
                    ArrayList<Gloss> defs = readGlosses.synsetToGlosses.get(synset.getSynsetId());
                    for (int j = 0; j < defs.size(); j++) {
                        Gloss def = defs.get(j);
                        synset.addDefinition(def);
                    }
                }
            }

            OutputStream fos = new FileOutputStream(pathToLmfFile+".test");
            wordnetLmfDataSaxParser.wordnetData.serialize(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
