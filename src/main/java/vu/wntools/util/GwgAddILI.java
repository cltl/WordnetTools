package vu.wntools.util;

import vu.wntools.lmf.Synset;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 05/06/15.
 */
public class GwgAddILI {

    ///Users/piek/Desktop/GWG/nl/odwn1.0.lmf.test

    static public void main (String[] args) {
        try {
            String pathToLmfFile = "";
            pathToLmfFile = "";
            String pathToIliFile  = "";
            pathToIliFile  = "";
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equals("--wn-lmf") && (args.length>(i+1))) {
                   pathToLmfFile = args[i+1];
                }
                else if (arg.equals("--ili") && (args.length>(i+1))) {
                   pathToIliFile = args[i+1];
                }
            }
            ReadILI readILI = new ReadILI();
            readILI.readILIFile(pathToIliFile);
            System.out.println("readILI.synsetToILIMap.size() = " + readILI.synsetToILIMap.size());
            WordnetLmfDataSaxParser wordnetLmfDataSaxParser = new WordnetLmfDataSaxParser();
            wordnetLmfDataSaxParser.parseFile(pathToLmfFile);

            Set keySet = wordnetLmfDataSaxParser.wordnetData.synsetMap.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                Synset synset = wordnetLmfDataSaxParser.wordnetData.synsetMap.get(key);
                String synsetID = synset.getSynsetId();
                if (synsetID.startsWith("eng-30-")) {
                    synsetID = "eng-"+synsetID.substring(7);
                }
                // System.out.println("synsetID = " + synsetID);
                if (readILI.synsetToILIMap.containsKey(synsetID)) {
                    String iliId = readILI.synsetToILIMap.get(synsetID);
                    synset.setIliId(iliId);
                    wordnetLmfDataSaxParser.wordnetData.synsetMap.put(synset.getSynsetId(), synset);
                }
                else {
                    //  System.out.println("Could not find synset.getSynsetId() = " + synset.getSynsetId());
                }
            }


            OutputStream fos = new FileOutputStream(pathToLmfFile+".ili");
            wordnetLmfDataSaxParser.wordnetData.serializeMap(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
