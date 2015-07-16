package vu.wntools.util;

import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by piek on 16/07/15.
 */
public class LabelBaseConceptFileWithData {

    static public void main (String[] args) {
        try {
            String pathToWordnetLmfFile = "/Users/piek/Desktop/odwn/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf";
            String pathToBaseConceptFile = "/Users/piek/Desktop/odwn/BC/bc-100-hypo-all-output-file.list";
            OutputStream fos = new FileOutputStream(pathToBaseConceptFile+".bc-labeled");
            WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
            wordnetLmfSaxParser.parseFile(pathToWordnetLmfFile);
            wordnetLmfSaxParser.wordnetData.buildSynsetIndex();

            FileInputStream fis = new FileInputStream(pathToBaseConceptFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String[] fields = inputLine.split(" ");
                    if (fields.length==2) {
                        String synsetId = fields[0];
                        String cnt = fields[1];
                        String synonyms = wordnetLmfSaxParser.wordnetData.getSynsetString(synsetId);
                        String str = synsetId+"\t" +synonyms + "\t"+cnt;
                        ArrayList<String> hypers = wordnetLmfSaxParser.wordnetData.hyperRelations.get(synsetId);
                        if (hypers!=null) {
                            for (int i = 0; i < hypers.size(); i++) {
                                String hyper = hypers.get(i);
                                String hyperSynonyms = wordnetLmfSaxParser.wordnetData.getSynsetString(synsetId);
                                str += "\t" + hyper + "\t" + hyperSynonyms;
                            }
                        }
                        str += "\n";
                        fos.write(str.getBytes());
                    }

                }
            }
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
