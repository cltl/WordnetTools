package vu.wntools.wordnet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/15/12
 * Time: 9:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CdbSynSaxParser extends DefaultHandler{

    public WordnetData wordnetData;
    private String value = "";
    private String aValue = "";
    private String aName = "";
    private String sourceId = "";
    private String targetId= "";
    private String targetRelation= "";
    private String posFilter = "";
    private String pos  = "";
    private boolean posMatch = true;
    private boolean IR = false;
    private ArrayList<String> relations = new ArrayList<String>();
    private ArrayList<String> hypers = new ArrayList<String>();
    private ArrayList<String> others = new ArrayList<String>();

    public CdbSynSaxParser() {
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

       // System.out.println("qName = " + qName);
        if (qName.equalsIgnoreCase("cdb_synset")) {
            hypers = new ArrayList<String>();
            others = new ArrayList<String>();
            sourceId = "";
            pos = "";
            posMatch = false;
            IR = false;
            aValue = attributes.getValue("c_sy_id");
            if (aValue!=null) {
                sourceId = aValue;

              //  System.out.println("sourceId = " + sourceId);
            }
            aValue = attributes.getValue("posSpecific");
            if (aValue!= null) {
               pos= aValue;
/*
               if (sourceId.equals("d_n-31719")) {
                   System.out.println("sourceId = " + sourceId);
                    System.out.println("pos = " + pos);
               }
*/
            }
            if (!posFilter.isEmpty()) {
                if (matchPos(posFilter, pos)) {
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
        else if (qName.equalsIgnoreCase("wn_internal_relations")) {
            IR = true;
        }
        else if (qName.equalsIgnoreCase("wn_equivalence_relations")) {
            IR = false;
        }
        else if (qName.equalsIgnoreCase("relation")) {
            if (posMatch) {
                if (IR) {
                    targetRelation="";
                    targetId = "";
                    aValue = attributes.getValue("relation_name");
                    if (aValue!=null) {
                        targetRelation = aValue;
                    }
                    aValue = attributes.getValue("target");
                    if (aValue!=null) {
                        targetId = aValue;
                    }
                    if (!targetId.isEmpty()) {
                        if (relations.size()==0) {
                            if ((targetRelation.equalsIgnoreCase("HAS_HYPERONYM"))      ||
                                (targetRelation.equalsIgnoreCase("NEAR_SYNONYM"))) {
                                hypers.add(targetId);
                            }
                            else if ((targetRelation.equalsIgnoreCase("XPOS_HAS_HYPERONYM"))      ||
                                    (targetRelation.equalsIgnoreCase("XPOS_NEAR_SYNONYM"))) {
                                others.add(targetId);
                            }
                        }
                        else if (relations.contains(targetRelation)) {
                            hypers.add(targetId);
                        }
                        else {
                            others.add(targetId);
                        }
                    }
                }
            }
        }
        else if (qName.equalsIgnoreCase("synonym")) {
            if (posMatch) {
                if (!sourceId.isEmpty()) {
                    for (int i = 0; i < attributes.getLength(); i++) {
                        aName = attributes.getQName(i);
                        aValue = attributes.getValue(i);
                        if (aName.equalsIgnoreCase("c_lu_id")) {
                           ////
                        }
                        else if (aName.equalsIgnoreCase("c_lu_id-previewtext")) {
                            int idx = aValue.indexOf(":");
                            String key = aValue.substring(0, idx);
                            if (wordnetData.entryToSynsets.containsKey(key)) {
                                ArrayList<String> synsets = wordnetData.entryToSynsets.get(key);
                                if (!synsets.contains(sourceId)) {
                                    synsets.add(sourceId);
                                    wordnetData.entryToSynsets.put(key, synsets);
                                }
                            }
                            else {
                                ArrayList<String> synsets = new ArrayList<String>();
                                synsets.add(sourceId);
                                wordnetData.entryToSynsets.put(key, synsets);
                            }
                        }

                    }
                }
            }
        }
        else {
        }
        value = "";
    }//--startElement


    /*
  <cdb_synset c_sy_id="c_100" posSpecific="" d_synset_id="" comment="">
        <synonyms>
                <synonym status="" c_cid_id="" c_lu_id-previewtext="balie:2" c_lu_id="r_n-5798"/>
        </synonyms>
        <base_concept>false</base_concept>
        <definition><![CDATA[leuning;]]></definition>
        <differentiae/>
        <wn_internal_relations>
                        <relation factive="" reversed="false" relation_name="HAS_HYPERONYM" target-previewtext="leuning:1" negative="false" coordinative="false" disjunctive="false" target="d_n-34833">
                                <author name="" score="0.0" status="" date="null" source_id=""/>
                        </relation>
        </wn_internal_relations>
        <wn_equivalence_relations>
                <relation target20-target20Previewtext="railing:1, rail:1" relation_name="EQ_HAS_HYPERNYM" version="" target30="ENG30-04047401-n" target20="ENG20-03894991-n">
                        <author name="" score="0.0" status="" date="null" source_id=""/>
                </relation>
        </wn_equivalence_relations>
        <wn_domains>
                <dom_relation name="piek" status="true" term="building_industry"/>
        </wn_domains>
        <vlis_domains/>
        <sumo_relations>
                <ont_relation name="piek" status="true" relation_name="+" negative="false" arg1="" arg2="Handle"/>
        </sumo_relations>
</cdb_synset>

    */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("cdb_synset")) {
            if ((!sourceId.isEmpty()) && hypers.size()>0) {
                wordnetData.addHyperRelation(sourceId, hypers);
/*
                if (hyperRelations.size()%100==0) {
                    System.out.println("hyperRelations = " + hyperRelations.size());
                }
*/
            }
            if ((!sourceId.isEmpty()) && others.size()>0) {
                wordnetData.addOtherRelations(sourceId, others);
            }
            sourceId = "";
            targetId = "";
            others = new ArrayList<String>();
            hypers = new ArrayList<String>();
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }

    boolean matchPos (String posFilter, String pos) {
        /// posFilter n,v,a,r,b
        if (posFilter.equalsIgnoreCase("n")) {
            if (pos.equalsIgnoreCase("noun")) {
                return true;
            }
            if (pos.equalsIgnoreCase("n")) {
                return true;
            }
            if (pos.toLowerCase().startsWith("n")) {
                return true;
            }
            if (pos.isEmpty()) {
                return true;
            }
        }
        else if (posFilter.equalsIgnoreCase("v")) {
            if (pos.equalsIgnoreCase("verb")) {
                return true;
            }
            if (pos.equalsIgnoreCase("v")) {
                return true;
            }
            if (pos.toLowerCase().startsWith("v")) {
                return true;
            }
        }
        else if (posFilter.equalsIgnoreCase("a")) {
            if (pos.equalsIgnoreCase("adjective")) {
                return true;
            }
            if (pos.equalsIgnoreCase("adj")) {
                return true;
            }
            if (pos.equalsIgnoreCase("a")) {
                return true;
            }
            if (pos.toLowerCase().startsWith("a")) {
                return true;
            }
        }
        else if (posFilter.equalsIgnoreCase("r")) {
            if (pos.equalsIgnoreCase("adverb")) {
                return true;
            }
            if (pos.equalsIgnoreCase("b")) {
                return true;
            }
            if (pos.equalsIgnoreCase("r")) {
                return true;
            }
        }
        return false;
    }

    static public void main (String[] args) {
        //String pathToFile = args[0];
        String pathToFile = "/Releases/wordnetsimilarity_v.0.1/resources/cdbsyn-latest.xml";
        ArrayList<String> relations = new ArrayList<String>();
        //relations.add("NEAR_SYNONYM");
        //relations.add("HAS_HYPERONYM");
        relations.add("HAS_MERO_PART");
        relations.add("HAS_HOLO_PART");

        CdbSynSaxParser parser = new CdbSynSaxParser();
        parser.setPos("a");
        parser.setRelations(relations);
        parser.parseFile(pathToFile);
        int depth = parser.wordnetData.getAverageDepthByWord();
        System.out.println("depth = " + depth);
        System.out.println("parser.wordnetData.entryToSynsets.size() = " + parser.wordnetData.entryToSynsets.size());
        System.out.println("parser.wordnetData.getHyperRelations().size() = " + parser.wordnetData.getHyperRelations().size());
        System.out.println("parser.wordnetData.getOtherRelations().size() = " + parser.wordnetData.getOtherRelations().size());
    }
}
