package vu.wntools.util;

import java.io.*;

/**
 * Created by piek on 10/16/14.
 */
public class AddMorphRelations {

//<Synset baseConcept="1" id="eng-30-04899031-n">
// <Definition gloss="appropriate conduct; doing the right thing" />
// <SynsetRelations>
// <SynsetRelation relType="has_hyperonym" target="eng-30-04898437-n" />
// </SynsetRelations>
// <MonolingualExternalRefs><MonolingualExternalRef externalReference="dummy" externalSystem="Domain" /></MonolingualExternalRefs>
// </Synset>

    /*
    arg1 sensekey	arg1 offset	relation	arg2 sensekey	arg2 offset	arg1 gloss (abbreviated)	arg2 gloss (abbreviated)
cannibalise%2:34:00::	201162291	agent	cannibal%1:18:00::	109891079	eat human flesh	a person who eats human flesh
cannibalize%2:34:00::	201162291	agent	cannibal%1:18:00::	109891079	eat human flesh	a person who eats human flesh

     */
    static void readMorphFile (String morphFile) throws IOException {
        FileInputStream fis = new FileInputStream(morphFile);
        OutputStream fos = new FileOutputStream(morphFile+".syn");
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader in = new BufferedReader(isr);
        String inputLine = "";
        if (in.ready()&&(inputLine = in.readLine()) != null) {
            /// processing the header
        }
        while (in.ready()&&(inputLine = in.readLine()) != null) {
            if (inputLine.trim().length() > 0) {
                String[] fields = inputLine.split("\t");
                if (fields.length>4) {
                    String sourceSynset = "eng-30-" + fields[1].substring(1) + "-v"; //201162291 -> eng-30-01162291-v
                    String targetSynset = "eng-30-" + fields[4].substring(1) + "-n"; //109891079 -> eng-30-09891079-n
                    String relationType = fields[2];
                    String str = "<Synset id=\"" + sourceSynset + "\">\n";
                    str += "<SynsetRelation relType=\"" + relationType + "\" target=\"" + targetSynset + "\"/>\n</Synset>\n";
                    fos.write(str.getBytes());
                }
            }
        }
        fos.close();
    }


    static public void main (String[] args) {
        //String pathToWnLmfFile = args[0];
       // String pathToMorphFile = args[1];
        String pathToWnLmfFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        String pathToMorphFile = "/Tools/wordnet-tools.0.1/resources/morphosemantic-links.txt";
        try {
            readMorphFile(pathToMorphFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
      /*  WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.parseFile(pathToWnLmfFile);
        try {
            OutputStream fos = new FileOutputStream(pathToWnLmfFile+".xpos.xml");
            //wordnetLmfSaxParser
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
