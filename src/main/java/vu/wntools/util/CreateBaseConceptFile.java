package vu.wntools.util;

import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 16/07/15.
 */
public class CreateBaseConceptFile {
    /*
    The next input files are required for executing the program:

	1) A file containing the hyponymy relations of the wordnet. It must have the next format:
		synsetA @ synsetB
		synsetB @ synsetC

	2) A file containing the number of all relations each synset has [OPTIONAL]:
	This file is required just in the case of selecting
	to take into account all the relations of the synsets
	for obtaining the BLC:
		synsetA 33
		synsetB 22
     */
    static public void main (String[] args) {
        try {
            String pathToWordnetLmfFile = "/Users/piek/Desktop/odwn/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf";
            OutputStream fos1 = new FileOutputStream(pathToWordnetLmfFile+".bc-rel");
            OutputStream fos2 = new FileOutputStream(pathToWordnetLmfFile+".bc-cnt");
            WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
            wordnetLmfSaxParser.parseFile(pathToWordnetLmfFile);
            wordnetLmfSaxParser.wordnetData.buildChildRelationsFromids();
            Set keySet = wordnetLmfSaxParser.wordnetData.hyperRelations.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String synset = keys.next();
                ArrayList<String> hypers = wordnetLmfSaxParser.wordnetData.hyperRelations.get(synset);
                for (int i = 0; i < hypers.size(); i++) {
                    String hyper = hypers.get(i);
                    String str = synset+" @ "+hyper+"\n";
                    fos1.write(str.getBytes());
                }
            }
            keySet = wordnetLmfSaxParser.wordnetData.childRelations.keySet();
            keys = keySet.iterator();
            while (keys.hasNext()) {
                String synset = keys.next();
                ArrayList<String> children = wordnetLmfSaxParser.wordnetData.childRelations.get(synset);
                String str = synset+" "+children.size()+"\n";
                fos2.write(str.getBytes());
            }


            fos1.close();
            fos2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
