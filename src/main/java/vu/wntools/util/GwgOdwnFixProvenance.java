package vu.wntools.util;

import vu.wntools.lmf.Synset;
import vu.wntools.lmf.SynsetRelation;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by piek on 05/06/15.
 */
public class GwgOdwnFixProvenance {

    ///Users/piek/Desktop/GWG/nl/odwn1.0.lmf.test
    ///Users/piek/Desktop/GWG/nl/odwn-translated-glosses.final

    static public void main (String[] args) {
        try {

            String pathToLmfFile = "";
            pathToLmfFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwn-lmf-Jul-2014/odwn1.0.glossed.lmf.xml";
            WordnetLmfDataSaxParser wordnetLmfDataSaxParser = new WordnetLmfDataSaxParser();
            wordnetLmfDataSaxParser.parseFile(pathToLmfFile);

            for (int i = 0; i < wordnetLmfDataSaxParser.wordnetData.getSynsets().size(); i++) {
                Synset synset = wordnetLmfDataSaxParser.wordnetData.getSynsets().get(i);
                for (int j = 0; j < synset.getRelations().size(); j++) {
                    SynsetRelation synsetRelation = synset.getRelations().get(j);
                    if (synsetRelation.getProvenance().isEmpty()) {
                        synsetRelation.setProvenance("odwn");
/*
                        if (synsetRelation.getTarget().startsWith("odwn-")) {
                            synsetRelation.setProvenance("odwn");
                        }
                        else if (synset.getSynsetId().startsWith("odwn-")) {
                            synsetRelation.setProvenance("odwn");
                        }
*/
                    }
                }
            }

            OutputStream fos = new FileOutputStream(pathToLmfFile+".fix");
            wordnetLmfDataSaxParser.wordnetData.serialize(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
