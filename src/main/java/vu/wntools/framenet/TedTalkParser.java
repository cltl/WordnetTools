package vu.wntools.framenet;

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
 * Created by piek on 26/03/2017.
 */
public class TedTalkParser  extends DefaultHandler {

    private String value = "";
    public  ArrayList<String> sentences = new ArrayList<String>();

    public TedTalkParser() {
        sentences = new ArrayList<String>();
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

        if (qName.equalsIgnoreCase("span")) {
        }

        else {
            //  System.out.println("qName = " + qName);
        }
        value = "";
    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("span")) {
            sentences.add(value);
            value = "";
        }
    }




    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }

    static public void main (String[] args) {
        String tedTalk = "/Users/piek/Desktop/DutchFrameNet/Ted-talk/talk.span.nl.xml";
        TedTalkParser tedTalkParser = new TedTalkParser();
        tedTalkParser.parseFile(tedTalk);
        try {
            OutputStream fos = new FileOutputStream(tedTalk+".txt");
            for (int i = 0; i < tedTalkParser.sentences.size(); i++) {
                String s = tedTalkParser.sentences.get(i);
                if (!s.isEmpty()) {
                    s += "\n";
                    fos.write(s.getBytes());
                }
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
