package vu.wntools.util;

import vu.wntools.lmf.Gloss;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by piek on 02/06/15.
 */
public class ReadGlosses {

    public HashMap<String, ArrayList<Gloss>> synsetToGlosses;

    public ReadGlosses () {
        synsetToGlosses = new HashMap<String, ArrayList<Gloss>>();

    }

    public void readGlossFile (String pathToILIfile, String language, String owner) {
        try {
            FileInputStream fis = new FileInputStream(pathToILIfile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            String ili = "";
            String target = "";
/*
odwn-10-106571003-n# regionale afdeling v.d. politie
odwn-10-108544665-n# spijsolie uit sojazaad
odwn-10-105700642-n# plan om vrede te bereiken
eng-30-02797881-n# elk v.d. vaste plaatsen in het sportveld
 */
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    // System.out.println("inputLine = " + inputLine);
                    String [] fields = inputLine.split("#");
                    if (fields.length>1) {
                        String synset = fields[0].trim().replaceAll("\"","");
                        String def = "";
                        for (int i = 1; i < fields.length; i++) {
                            String field = fields[i];
                            def += field+" ";
                        }
                        Gloss gloss = new Gloss();
                        gloss.setLanguage(language);
                        gloss.setProvenance(owner);
                        gloss.setText(def.trim());
                        if (synsetToGlosses.containsKey(synset)) {
                            ArrayList<Gloss> defs = synsetToGlosses.get(synset);
                            defs.add(gloss);
                            synsetToGlosses.put(synset, defs);
                        }
                        else {
                            ArrayList<Gloss> defs = new ArrayList<Gloss>();
                            defs.add(gloss);
                            synsetToGlosses.put(synset, defs);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
