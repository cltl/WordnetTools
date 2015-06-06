package vu.wntools.wordnet;

import vu.wntools.lmf.Synset;
import vu.wntools.util.ReadILI;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by piek on 05/06/15.
 */
public class GwgAddILI {

    ///Users/piek/Desktop/GWG/nl/odwn1.0.lmf.test

    static public void main (String[] args) {
        try {
            String glossLanguage = "en";
            String pathToLmfFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf";
            String pathToIliFile  = "/Users/piek/Desktop/GWG/ili.ttl";
            ReadILI readILI = new ReadILI();
            readILI.readILIFile(pathToIliFile);
            System.out.println("readILI.synsetToILIMap.size() = " + readILI.synsetToILIMap.size());
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
            }

            OutputStream fos = new FileOutputStream(pathToLmfFile+".test");
            wordnetLmfDataSaxParser.wordnetData.serialize(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
