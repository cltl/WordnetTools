package vu.wntools.util;

import vu.wntools.lmf.Synset;
import vu.wntools.lmf.SynsetRelation;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 05/07/15.
 */
public class AddFrameNetWordnetMappings {

        /*
    FRAME;LU;SYNSET ID;;;;;;;;;;ESO: NOT
Arriving;appear.v;200422090;;;;;;;;;;
Arriving;approach.n;100280853;;;;;;;;;;
Arriving;approach.v;202053941;;;;;;;;;;
Arriving;arrival.n;100048374;;;;;;;;;;
Arriving;arrive.v;202005948;;;;;;;;;;
Arriving;come.v;202005948;;;;;;;;;;
Arriving;crest.v;_;;;;;;;;;;
Arriving;descend_on.v;_;;;;;;;;;;
Arriving;enter.v;202016523;;;;;;;;;;
Arriving;entrance.n;100049003;107370125;;;;;;;;;
Arriving;entry.n;100049003;;;;;;;;;;
Arriving;get.v;202005948;;;;;;;;;;
Arriving;hit.v;202020590;;;;;;;;;;
Arriving;influx.n;_;;;;;;;;;;
Arriving;make_it.v;_;;;;;;;;;;
Arriving;make.v;202020590;;
Arriving;reach.v;202020590;;
Arriving;return.n;_;;
Arriving;return.v;202004874;202078294;
Arriving;visit.n;_;;
Arriving;visit.v;_;;
Vehicle_landing;land.v;201979901;201981036;
Vehicle_landing;set_down.v;201979901;;
Vehicle_landing;touch_down;201979702;;

     */
    static HashMap<String, ArrayList<String>> readMappingFile (String pathToFile) throws IOException {
        HashMap<String, ArrayList<String>> mapping = new HashMap<String, ArrayList<String>>();
        FileInputStream fis = new FileInputStream(pathToFile);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader in = new BufferedReader(isr);
        String inputLine = "";
        while (in.ready() && (inputLine = in.readLine()) != null) {
            if (inputLine.trim().length() > 0) {
                String[] fields = inputLine.split(";");
                if (fields.length > 2) {
                    String frame = fields[0];
                   // String lu = fields[1];
                    String pos = "n";
                    for (int i = 2; i < fields.length; i++) {
                        String field = fields[i];
                        if (field.length()==9) {
                            if (field.startsWith("1")) {
                                pos = "-n";
                            } else if (field.startsWith("2")) {
                                pos = "-v";
                            }
                            field = field.substring(1); //// take off the pos code
                            String synset = "eng-30-" + field + pos;
                            if (mapping.containsKey(synset)) {
                                ArrayList<String> frames = mapping.get(synset);
                                if (!frames.contains(frame)) {
                                    frames.add(frame);
                                    mapping.put(synset, frames);
                                }
                            } else {
                                ArrayList<String> frames = new ArrayList<String>();
                                frames.add(frame);
                                mapping.put(synset, frames);
                            }
                        }
                    }
                }

            }
        }
        return mapping;
    }

    static public void main (String[] args) {
        String pathToMappingFile = "/Users/piek/Desktop/ESO/eso-mappings.txt";

        try {
            OutputStream fos = new FileOutputStream(pathToMappingFile+".synsets");
            HashMap<String, ArrayList<String>> mappings = readMappingFile(pathToMappingFile);
            System.out.println("mappings.size() = " + mappings.size());
            Set keySet = mappings.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                ArrayList<String> frames = mappings.get(key);
                String str = "<Synset id=\""+key+"\">\n";
                for (int i = 0; i < frames.size(); i++) {
                    String frame = frames.get(i);
                         /*
                            <SynsetRelation relType="has_hyperonym" target="eso:Arriving"/>
                             */
                    str += "<SynsetRelation relType=\"has_hyperonym\" target=\"fn:"+frame+"\"/>\n";
                }
                str += "</Synset>\n";
                fos.write(str.getBytes());

            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static public void main_merge (String[] args) {
        String pathToWnLmfFile = "/Code/vu/newsreader/EventCoreference/newsreader-vm/vua-eventcoreference_v4_2015/resources/wneng-30.lmf.xml.xpos";
        String pathToMappingFile = "/Users/piek/Desktop/ESO/eso-mappings.txt";
        WordnetLmfDataSaxParser wordnetLmfDataSaxParser = new WordnetLmfDataSaxParser();
        wordnetLmfDataSaxParser.parseFile(pathToWnLmfFile);
        try {
            HashMap<String, ArrayList<String>> mappings = readMappingFile(pathToMappingFile);
            System.out.println("mappings.size() = " + mappings.size());
            Set keySet = mappings.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                ArrayList<String> frames = mappings.get(key);
                if (wordnetLmfDataSaxParser.wordnetData.synsetMap.containsKey(key)) {
                    Synset synset = wordnetLmfDataSaxParser.wordnetData.synsetMap.get(key);
                    for (int i = 0; i < frames.size(); i++) {
                        String frame = "fn:"+ frames.get(i);
                        SynsetRelation synsetRelation = new SynsetRelation();
                        synsetRelation.setRelType("has_hyperonym");
                        synsetRelation.setRelType(frame);
                        synset.addRelations(synsetRelation);
                      //  System.out.println("synsetRelation.toString() = " + synsetRelation.toString());
                    }
                    int idx = wordnetLmfDataSaxParser.wordnetData.synsets.indexOf(synset);
                    wordnetLmfDataSaxParser.wordnetData.synsets.add(idx, synset);
                }
                else {
                    Synset synset = new Synset();
                    synset.setSynsetId(key);
                    for (int i = 0; i < frames.size(); i++) {
                        String frame = "fn:"+ frames.get(i);
                        SynsetRelation synsetRelation = new SynsetRelation();
                        synsetRelation.setRelType("has_hyperonym");
                        synsetRelation.setRelType(frame);
                        synset.addRelations(synsetRelation);
                        //  System.out.println("synsetRelation.toString() = " + synsetRelation.toString());
                    }
                    wordnetLmfDataSaxParser.wordnetData.synsets.add(synset);
                    wordnetLmfDataSaxParser.wordnetData.synsetMap.put(key, synset);

                }
            }
            OutputStream fos = new FileOutputStream(pathToWnLmfFile+".extended");
            wordnetLmfDataSaxParser.wordnetData.serialize(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
