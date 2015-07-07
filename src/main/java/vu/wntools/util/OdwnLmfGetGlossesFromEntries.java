package vu.wntools.util;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import vu.wntools.lmf.*;
import vu.wntools.wordnet.WordnetLmfData;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class OdwnLmfGetGlossesFromEntries extends DefaultHandler {

    public WordnetLmfData wordnetData;
    private String value = "";
    private LmfEntry lmfEntry;
    private Synset synset;
    private Gloss definition;
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


    public OdwnLmfGetGlossesFromEntries() {
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
    <cdb_synset c_sy_id="eng-30-05258627-n" posSpecific="NOUN" comment="EQ_NEAR_SYNONYM">
	<synonyms>
		<synonym c_lu_id="o_n-106836869" c_lu_id-previewtext="spuuglok:1" status="cdb2.2_Auto"/>
	</synonyms>
	<definition>a spiral curl plastered on the forehead or cheek</definition>
	<wn_internal_relations>
			<relation relation_name="HAS_HYPERONYM" target="eng-30-05258299-n" source="pwn"/>
	</wn_internal_relations>
</cdb_synset>
     */
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {

        //<Lexicon label="Princeton WordNet 3.0" language="eng" languageCoding="ISO 639-3" owner="KYOTO project" version="3.0">


        if (qName.equalsIgnoreCase("cdb_synset")) {
            synset = new Synset();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("c_sy_id")) {
                    synset.setSynsetId(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("ili")) {
                    synset.setIliId(attributes.getValue(i).trim());
                }
            }
        }
        else if (qName.equalsIgnoreCase("relation")) {
            synsetRelation = new SynsetRelation();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("target")) {
                    synsetRelation.setTarget(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("relation_name")) {
                    synsetRelation.setRelType(attributes.getValue(i).trim());
                }
                else if (attributes.getQName(i).equalsIgnoreCase("source")) {
                    synsetRelation.setProvenance(attributes.getValue(i).trim());
                }

            }
            synset.addRelations(synsetRelation);
        }
        else if (qName.equalsIgnoreCase("Definition")) {
            definition = new Gloss();
        }
        else {
        }
        value = "";

    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {


        if (qName.equalsIgnoreCase("cdb_synset")) {
           wordnetData.addSynset(synset);
        }
        else if (qName.equalsIgnoreCase("Definition")) {
            if (synset.getSynsetId().startsWith("own")) {
                definition.setLanguage("nl");
                definition.setProvenance("odwn");
            }
            else {
                definition.setLanguage("en");
                definition.setProvenance("pwn");
            }
            synset.addDefinition(definition);
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }

    static public void main (String[] args) {
        try {
            String pathToCdbFile = "/Users/piek/Desktop/GWG/nl/odwn_1.0.xml";
            String pathToRbnLmfFile = "/Users/piek/Desktop/GWG/nl/odwn_orbn-LMF.xml";

            WordnetLmfDataSaxParser wordnetLmfDataSaxParser = new WordnetLmfDataSaxParser();
            wordnetLmfDataSaxParser.parseFile(pathToRbnLmfFile);
/*
            OdwnCdbtoGWG wordnetDataSaxParser = new OdwnCdbtoGWG();
            wordnetLmfDataSaxParser.parseFile(pathToCdbFile);
*/
            OutputStream fosGlossMap = new FileOutputStream(pathToCdbFile+".source-gloss");
            wordnetLmfDataSaxParser.wordnetData.setGlobalLabel("ODWN-1.0");
            wordnetLmfDataSaxParser.wordnetData.setLexiconLabel("ODWN-1.0");
            for (int i = 0; i < wordnetLmfDataSaxParser.wordnetData.getSynsets().size(); i++) {
                Synset synset = wordnetLmfDataSaxParser.wordnetData.getSynsets().get(i);
                String synsetID = synset.getSynsetId();
                if (wordnetLmfDataSaxParser.wordnetData.synsetToEntriesMap.containsKey(synsetID)) {
                    ArrayList<String> entries = wordnetLmfDataSaxParser.wordnetData.synsetToEntriesMap.get(synsetID);
                    for (int j = 0; j < entries.size(); j++) {
                        String entry = entries.get(j);
                        ArrayList<LmfEntry> lmfEntries = wordnetLmfDataSaxParser.wordnetData.entryMap.get(entry);
                        for (int k = 0; k < lmfEntries.size(); k++) {
                            LmfEntry lmfEntry = lmfEntries.get(k);
                            for (int l = 0; l < lmfEntry.getSenses().size(); l++) {
                                LmfSense lmfSense = lmfEntry.getSenses().get(l);
                                if (lmfSense.getSynset().equals(synsetID)) {
                                    if (!lmfSense.getDefinition().isEmpty()) {
                                        Gloss gloss = new Gloss();
                                        gloss.setLanguage("nl");
                                        gloss.setProvenance("odwn");
                                        gloss.setText(lmfSense.getDefinition());
                                        synset.addDefinition(gloss);
                                        String str = "\""+synset.getSynsetId()+"\""+" # "+ gloss.getText()+"\n";
                                        fosGlossMap.write(str.getBytes());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            fosGlossMap.close();
            OutputStream fos = new FileOutputStream(pathToCdbFile+".lmf");
            wordnetLmfDataSaxParser.wordnetData.serialize(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
