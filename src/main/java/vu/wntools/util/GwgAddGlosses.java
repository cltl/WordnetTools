package vu.wntools.util;

import vu.wntools.lmf.Gloss;
import vu.wntools.lmf.Synset;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 05/06/15.
 */
public class GwgAddGlosses {

    ///Users/piek/Desktop/GWG/nl/odwn1.0.lmf.test
    ///Users/piek/Desktop/GWG/nl/odwn-translated-glosses.final

    static public void main (String[] args) {
        try {
/*
            String glossLanguage = "en";
            String glossOwner = "google-translate";
            String pathToLmfFile = "/Users/piek/Desktop/GWG/nl/odwn1.0.lmf.test";
            String pathToGlossFile = "/Users/piek/Desktop/GWG/nl/odwn-translated-glosses.final";
*/
            String glossLanguage = "";
            String glossOwner = "";
            String pathToLmfFile = "";
            String pathToGlossFile = "";
/*
            String glossLanguage = "en";
            String glossOwner = "pwn";
            String pathToLmfFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/odwn_1.0.xml.lmf";
            String pathToGlossFile = "/Users/piek/Desktop/GWG/nl/wn-eng-glosses";
*/
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equals("--wn-lmf") && (args.length>(i+1))) {
                    pathToLmfFile = args[i+1];
                }
                else if (arg.equals("--gloss") && (args.length>(i+1))) {
                    pathToGlossFile = args[i+1];
                }
                else if (arg.equals("--language") && (args.length>(i+1))) {
                    glossLanguage = args[i+1];
                }
                else if (arg.equals("--provenance") && (args.length>(i+1))) {
                    glossOwner = args[i+1];
                }
            }
            ReadGlosses readGlosses = new ReadGlosses();
            readGlosses.readGlossFile(pathToGlossFile, glossLanguage, glossOwner);
            System.out.println("readGlosses.synsetToGlosses.size() = " + readGlosses.synsetToGlosses.size());
            WordnetLmfDataSaxParser wordnetLmfDataSaxParser = new WordnetLmfDataSaxParser();
            wordnetLmfDataSaxParser.parseFile(pathToLmfFile);
            Set keySet = wordnetLmfDataSaxParser.wordnetData.synsetMap.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                Synset synset = wordnetLmfDataSaxParser.wordnetData.synsetMap.get(key);
                if (readGlosses.synsetToGlosses.containsKey(synset.getSynsetId())) {
                    ArrayList<Gloss> defs = readGlosses.synsetToGlosses.get(synset.getSynsetId());
                    for (int j = 0; j < defs.size(); j++) {
                        Gloss def = defs.get(j);
                        synset.addDefinition(def);
                        //System.out.println("def = " + def);
                    }
                    wordnetLmfDataSaxParser.wordnetData.synsetMap.put(synset.getSynsetId(), synset);
                }
                else {
                  //  System.out.println("Could not find synset.getSynsetId() = " + synset.getSynsetId());
                }
            }
            /*for (int i = 0; i < wordnetLmfDataSaxParser.wordnetData.getSynsets().size(); i++) {
                Synset synset = wordnetLmfDataSaxParser.wordnetData.getSynsets().get(i);
                if (readGlosses.synsetToGlosses.containsKey(synset.getSynsetId())) {
                    ArrayList<Gloss> defs = readGlosses.synsetToGlosses.get(synset.getSynsetId());
                    for (int j = 0; j < defs.size(); j++) {
                        Gloss def = defs.get(j);
                        synset.addDefinition(def);
                    }
                }
                else {
                    System.out.println("Could not find synset.getSynsetId() = " + synset.getSynsetId());
                }
            }*/

            OutputStream fos = new FileOutputStream(pathToLmfFile+"."+glossOwner);
           // wordnetLmfDataSaxParser.wordnetData.serialize(fos);
            wordnetLmfDataSaxParser.wordnetData.serializeMap(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
