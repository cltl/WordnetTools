package vu.wntools.util;

import eu.kyotoproject.kaf.KafTerm;
import eu.kyotoproject.kaf.KafWordForm;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by piek on 16/04/15.
 */
public class CatParser extends DefaultHandler {

    public class Annotation {
        private String attribute;
        private String value;
        public  Annotation () {
               this.attribute = "";
               this.value = "";
        }

        public Annotation(String attribute, String value) {
            this.attribute = attribute;
            this.value = value;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public class Relation {
        private String source;
        private String target;
        private String relationId;

        public Relation() {
            this.source = "";
            this.target = "";
            this.relationId = "";
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getRelationId() {
            return relationId;
        }

        public void setRelationId(String relationId) {
            this.relationId = relationId;
        }
    }
        public ArrayList<Relation> relationList;
        public TreeSet<String> attributeList;
        public String annotationName ="";
        public String relationnName ="";
        String source = "";
        String target = "";
        HashMap<String, ArrayList<String>> termToTokens;
        HashMap<String, String> tokenToTerm;
        ArrayList<Annotation> annotationArrayList;
        String value = "";
        KafWordForm kafWordForm;
        public ArrayList<KafWordForm> kafWordFormArrayList;
        KafTerm kafTerm;
        public ArrayList<KafTerm> kafTermArrayList;
        ArrayList<String> spans;
        public HashMap<String, ArrayList<Annotation>> annotations;
        String span = "";
        String relationId ="";
        boolean REFERSTO = false;

        public CatParser (String name) {
             init(name);
        }
        void init(String name) {
            annotationName = name;
            relationList = new ArrayList<Relation>();
            attributeList = new TreeSet<String>();
            termToTokens = new HashMap<String, ArrayList<String>>();
            tokenToTerm = new HashMap<String, String>();
            annotationArrayList = new ArrayList<Annotation>();
            value = "";
            kafWordForm = new KafWordForm();
            kafWordFormArrayList = new ArrayList<KafWordForm>();
            kafTerm = new KafTerm();
            kafTermArrayList = new ArrayList<KafTerm>();
            annotations = new HashMap<String, ArrayList<Annotation>>();
            REFERSTO = false;
            target = "";
            source = "";
            relationId ="";
        }

        public void parseFile(String filePath) {
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
            //System.out.println("myerror = " + myerror);
        }//--c



        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes)
                throws SAXException {

           // System.out.println("qName = " + qName);


            if (qName.equalsIgnoreCase(annotationName)) {
                kafTerm = new KafTerm();
                String mentionId = attributes.getValue("m_id").trim();
                kafTerm.setTid(mentionId);
                annotationArrayList = new ArrayList<Annotation>();
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (!attributes.getQName(i).equalsIgnoreCase("m_id")) {
                        if (!attributeList.contains(attributes.getQName(i))) {
                            attributeList.add(attributes.getQName(i));
                        }
                        Annotation annotation = new Annotation(attributes.getQName(i),attributes.getValue(i).trim());
                        annotationArrayList.add(annotation);
                    }
                }
            }
            else if (qName.equalsIgnoreCase("token")) {
                kafWordForm = new KafWordForm();
                kafWordForm.setWid(attributes.getValue("t_id"));
                Integer sentenceInt = Integer.parseInt(attributes.getValue("sentence"));
                /// HACK TO MAKE SENTENCE ID EQUAL TO SENTENCE ID OF WIKINEWS NAF GENERATED BY FBK
                sentenceInt++;
                kafWordForm.setSent(sentenceInt.toString());
            }
            else if (qName.equalsIgnoreCase("token_anchor")) {
                span = attributes.getValue("t_id");
                kafTerm.addSpans(span);
                tokenToTerm.put(span, kafTerm.getTid());
                if (termToTokens.containsKey(kafTerm.getTid())) {
                    ArrayList<String> tokens = termToTokens.get(kafTerm.getTid());
                    tokens.add(span);
                    termToTokens.put(kafTerm.getTid(), tokens);
                }
                else {
                    ArrayList<String> tokens = new ArrayList<String>();
                    tokens.add(span);
                    termToTokens.put(kafTerm.getTid(), tokens);
                }
            }
            else if (qName.equalsIgnoreCase("source")) {
                source = attributes.getValue("m_id");
            }
            else if (qName.equalsIgnoreCase("target")) {
                target = attributes.getValue("m_id");
            }
            else if (!relationnName.isEmpty()) {
                if (qName.equalsIgnoreCase(relationnName)) {
                    relationId = attributes.getValue("r_id").trim();
                }
            }
            else {
              //  System.out.println("qName = " + qName);
            }
            value = "";
        }//--startElement


        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (qName.equalsIgnoreCase("token")) {
                kafWordForm.setWf(value.trim());
                kafWordFormArrayList.add(kafWordForm);
                kafWordForm = new KafWordForm();
            }
            else if (qName.equalsIgnoreCase("annotation")) {
                kafTermArrayList.add(kafTerm);
                annotations.put(kafTerm.getTid(), annotationArrayList);
            }
            else if (!relationnName.isEmpty()) {
                if (qName.equalsIgnoreCase(relationnName)) {
                    Relation relation = new Relation();
                    relation.setRelationId(relationId);
                    relation.setSource(source);
                    relation.setTarget(target);
                    relationList.add(relation);
                }
            }
        }




        public void characters(char ch[], int start, int length)
                throws SAXException {
            value += new String(ch, start, length);
            // System.out.println("tagValue:"+value);
        }

        public String attributeListToString () {
            String str = "";
            Iterator<String> keys = attributeList.iterator();
            while (keys.hasNext()) {
                str += "\tCAT:"+keys.next();
            }
            return str;
        }

        public String relationListToString (String termId) {
            String str = "";
            for (int i = 0; i < relationList.size(); i++) {
                Relation relation = relationList.get(i);
                if (relation.getSource().equals(termId)) {
                    str = "source:";
                    ArrayList<String> tokens = termToTokens.get(relation.getTarget());
                    for (int j = 0; j < tokens.size(); j++) {
                        String token = tokens.get(j);
                        str += token+";";
                    }
                }
                else if (relation.getTarget().equals(termId)) {
                    str = "target:";
                    ArrayList<String> tokens = termToTokens.get(relation.getSource());
                    for (int j = 0; j < tokens.size(); j++) {
                        String token = tokens.get(j);
                        str += token+";";
                    }
                }
            }
            return str;
        }
        public String annotationsToString (ArrayList<Annotation> annotations) {
            String str = "";
            Iterator<String> keys = attributeList.iterator();
            while (keys.hasNext()) {
                String attr = keys.next();
                str += "\t";
                for (int i = 0; i < annotations.size(); i++) {
                    Annotation annotation = annotations.get(i);
                    if (annotation.getAttribute().equalsIgnoreCase(attr)) {
                        String value = annotation.getValue();
                        if (!value.isEmpty()) {
                            String [] fields = value.split(" ");
                            str += fields[0].trim();
                        }
                    }
                }
            }
            return str;
        }

}
