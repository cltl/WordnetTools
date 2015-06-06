package vu.wntools.wordnet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/15/12
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PwnSaxParser extends DefaultHandler {
    static OutputStream fos = null;
    public WordnetData wordnetData;
    private String value = "";
    private String sourceId = "";
    private String targetId= "";
    private String entry = "";
    private String type = "";
    private String posFilter = "";
    private String pos = "";
    private String definition = "";
    private boolean posMatch = true;
    private ArrayList<String> relations = new ArrayList<String>();
    private ArrayList<String> hypers = new ArrayList<String>();
    private ArrayList<String> others = new ArrayList<String>();

    public PwnSaxParser() {
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

    public void parseFile(String filePath) {
            System.out.println("filePath = " + filePath);
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
            System.out.println("myerror = " + myerror);
        }//--c


        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes)
                throws SAXException {

            if (qName.equalsIgnoreCase("ILR")) {
                type = attributes.getValue("type");
            }
            else if (qName.equalsIgnoreCase("SYNSET")) {
                pos = "";
                sourceId = "";
                entry = "";
                definition = "";
            }
            value = "";
        }//--startElement


        /*

    <SYNSET>
    <ID>eng-30-00001740-a</ID>
    <POS>a</POS><SYNONYM>
    <LITERAL sense="1">able</LITERAL>
    <WORD>able</WORD></SYNONYM>
    <ILR type="near_antonym">eng-30-00002098-a</ILR>
    <ILR type="be_in_state">eng-30-05200169-n</ILR>
    <ILR type="be_in_state">eng-30-05616246-n</ILR>
    <ILR type="eng_derivative">eng-30-05200169-n</ILR>
    <ILR type="eng_derivative">eng-30-05616246-n</ILR>
    <DEF>(usually followed by `to') having the necessary means or skill or know-how or authority to do something</DEF>
    <USAGE>able to swim</USAGE><USAGE>she was able to program her computer</USAGE>
    <USAGE>we were at last able to buy a car</USAGE>
    <USAGE>able to get a grant for the project</USAGE>
    </SYNSET>

        */

        /*
       <SYNSET>
       <STAMP>fra82e3 2010-10-20 15:22:44</STAMP>
       <RIGIDITY rigidScore="0" rigid="false" nonRigidScore="0"/>
       <ILR type="hypernym">eng-30-10058777-n</ILR>
       <ILR type="eng_derivative">eng-30-01097031-v</ILR>
       <ILR type="eng_derivative">eng-30-01518694-a</ILR>
       <ILR type="eng_derivative">eng-30-05640184-n</ILR>
       <ID>eng-30-10622053-n</ID>
       <SYNONYM><LITERAL sense="1">soldier</LITERAL></SYNONYM>
       <DEF>an enlisted man or woman who serves in an army</DEF>
       <USAGE>the soldiers stood at attention</USAGE>
       <VERSION>642</VERSION>
       <POS>n</POS>
       </SYNSET>
        */
        ///** version that filters entries on pos ***////

        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            if (qName.equalsIgnoreCase("SYNSET")) {
                /// add all relations
                if ((!sourceId.isEmpty()) && hypers.size()>0) {
                    wordnetData.addHyperRelation(sourceId, hypers);
                }
                if ((!sourceId.isEmpty()) && others.size()>0) {
                    wordnetData.addOtherRelations(sourceId, others);
                }
                /// possibly filter entries
                if (posMatch && !entry.isEmpty()) {
                    if (wordnetData.entryToSynsets.containsKey(entry)) {
                        ArrayList<String> synsets = wordnetData.entryToSynsets.get(entry);
                        if (!synsets.contains(sourceId)) {
                            synsets.add(sourceId);
                            wordnetData.entryToSynsets.put(entry, synsets);
                        }
                    }
                    else {
                        ArrayList<String> synsets = new ArrayList<String>();
                        synsets.add(sourceId);
                        wordnetData.entryToSynsets.put(entry, synsets);
                    }
                }
                others = new ArrayList<String>();
                hypers = new ArrayList<String>();
                entry = "";
                pos = "";
                sourceId = "";
            }
            else if (qName.equalsIgnoreCase("POS")) {
                pos = value.trim();
                if (!posFilter.isEmpty()) {
                    if (pos.equalsIgnoreCase(posFilter)) {
                        posMatch = true;
                    }
                    else {
                        posMatch = false;
                    }
                }
                else {
                    posMatch = true;
                }
            }
            else if (qName.equalsIgnoreCase("DEF")) {
                definition = value.trim();
                String str = sourceId+" # "+definition+"\n";
                try {
                    fos.write(str.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (qName.equalsIgnoreCase("ILR")) {
                    targetId = value.trim();
                    if (relations.size()==0) {
                        if (type.equalsIgnoreCase("hypernym")) {
                            hypers.add(targetId);
                        }
                        else if (type.equalsIgnoreCase("eng_derivative")) {
                            others.add(targetId);
                        }
                    }
                    else if (relations.contains(type)) {
                        hypers.add(targetId);
                    }
                    else {
                        others.add(targetId);
                    }
                    type = "";
            }
            else if (qName.equalsIgnoreCase("LITERAL")) {
                 entry = value.trim();
            }
            else if (qName.equalsIgnoreCase("ID")) {
                sourceId = value.trim();
            }

        }


        public void characters(char ch[], int start, int length)
                throws SAXException {
            value += new String(ch, start, length);
            // System.out.println("tagValue:"+value);
        }

        static public void main (String[] args) {
            //String pathToFile = args[0];
            String pathToFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.gwg.xml";
            PwnSaxParser parser = new PwnSaxParser();
            ArrayList<String> relations = new ArrayList<String>();
            //relations.add("NEAR_SYNONYM");
            //relations.add("HAS_HYPERONYM");
            //relations.add("HAS_MERO_PART");
            //relations.add("HAS_HOLO_PART");
            //relations.add("eng_derivative");
         //   relations.add("hypernym");

          //  parser.setPos("n");
          //  parser.setRelations(relations);
          //  parser.parseFile(pathToFile);
/*
            int depth = parser.wordnetData.getAverageDepthByWord();
            System.out.println("depth = " + depth);
            System.out.println("parser.wordnetData.entryToSynsets.size() = " + parser.wordnetData.entryToSynsets.size());
            System.out.println("parser.wordnetData.getHyperRelations().size() = " + parser.wordnetData.getHyperRelations().size());
            System.out.println("parser.wordnetData.getOtherRelations().size() = " + parser.wordnetData.getOtherRelations().size());
*/
            try {
                fos = new FileOutputStream("/Users/piek/Desktop/wn-eng-glosses");
                parser.parseFile(pathToFile);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
