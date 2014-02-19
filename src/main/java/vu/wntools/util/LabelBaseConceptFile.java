package vu.wntools.util;

import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by piek on 2/19/14.
 */
public class LabelBaseConceptFile {


    static public void main (String[] args) {
       // String pathToWordNetLmfFile = args[0];
       // String pathToBaseConceptFile = args[1];
        String pathToWordNetLmfFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        String pathToBaseConceptFile = "/Code/vu/kyotoproject/OntoTagger/release/ontotagger-v1.0/resources/en/base-concept-label/T1.labelled.txt";
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.parseFile(pathToWordNetLmfFile);
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
            try {
                ArrayList<String> bcs = new ArrayList<String>();
                FileOutputStream fos = new FileOutputStream(pathToBaseConceptFile+".lbl.txt");
                FileOutputStream fosbcs = new FileOutputStream(pathToBaseConceptFile+".bc.txt");
                FileInputStream fis = new FileInputStream(pathToBaseConceptFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine = "";
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    if (inputLine.trim().length()>0) {

                        /*
                        dw-eng-30-100-n eng-30-04341686-n
dw-eng-30-101-n eng-30-03391770-n
dw-eng-30-102-n eng-30-04341686-n
                         */
                        String [] fields = inputLine.split(" ");
                        if (fields.length==2) {
                            String synsetId = fields[0].trim();
                            String bc = fields[1].trim();
                            if (wordnetLmfSaxParser.wordnetData.synsetToEntries.containsKey(bc)) {
                                ArrayList<String> entries = wordnetLmfSaxParser.wordnetData.synsetToEntries.get(bc);
                                for (int i = 0; i < entries.size(); i++) {
                                    String s = entries.get(i);
                                    bc += "-"+s;
                                }
                                if (!bcs.contains(bc)) {
                                    bcs.add(bc);
                                }
                            }

                            String str = synsetId+" "+bc+"\n";
                            fos.write(str.getBytes());
                        }
                    }
                }
                in.close();
                fos.close();
                for (int i = 0; i < bcs.size(); i++) {
                    String s = bcs.get(i)+"\n";
                    fosbcs.write(s.getBytes());

                }
                fosbcs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
