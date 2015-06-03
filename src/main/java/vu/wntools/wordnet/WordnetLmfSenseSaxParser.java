package vu.wntools.wordnet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by piek on 01/06/15.
 */
public class WordnetLmfSenseSaxParser extends DefaultHandler{

    private String value = "";
    public HashMap<String, ArrayList<String>> synsetDefinitionMap = new HashMap<String, ArrayList<String>>();
    /*
    <Sense senseId="o_n-107531373" synset="eng-30-06868986-n" definition="zesde toon van de diatonische en tiende toon van de chromatische toonschaal, uitgaande van de grondtoon C">
        <SenseRelations/><Semantics-noun countability="count"/>
        <Pragmatics/>
        <SenseExamples>
          <SenseExample id="o_n-107531373-1">
            <textualForm textualform="de toon A" phraseType="np"/>
            <Semantics_ex/>
            <Pragmatics/>
          </SenseExample>
        </SenseExamples>
      </Sense>
     */

    public WordnetLmfSenseSaxParser() {
        this.synsetDefinitionMap = new HashMap<String, ArrayList<String>>();
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

    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {

        //<Lexicon label="Princeton WordNet 3.0" language="eng" languageCoding="ISO 639-3" owner="KYOTO project" version="3.0">

       if (qName.equalsIgnoreCase("Sense")) {
            String synsetId = "";
            String definition = "";
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equalsIgnoreCase("synset")) {
                    synsetId = attributes.getValue(i).trim();
                }
                if (attributes.getQName(i).equalsIgnoreCase("definition")) {
                    definition = attributes.getValue(i).trim();
                }
            }
            if (!synsetId.isEmpty() && !definition.isEmpty()) {
                if (synsetDefinitionMap.containsKey(synsetId)) {
                    ArrayList<String> defs = synsetDefinitionMap.get(synsetId);
                    if (!defs.contains(definition)) {
                        defs.add(definition);
                        synsetDefinitionMap.put(synsetId, defs);
                    }
                } else {
                    ArrayList<String> defs = new ArrayList<String>();
                    defs.add(definition);
                    synsetDefinitionMap.put(synsetId, defs);
                }
            }
        }
        value = "";

    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {


    }//--endElement

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }
}
