package vu.wntools.util;

import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by piek on 08/07/15.
 */
public class ClassifyWordList {
    static public void main (String[] args) {

        String pathToWordFile ="";
        String pathToWNFile = "";
        pathToWordFile ="/Users/piek/Desktop/odwn/verbs_sonar.txt";
        // pathToWNFile = "/Users/piek/Desktop/NWR/NWR-benchmark/coreference/vua-eventcoreference_v2_2014/resources/wneng-30.lmf.xml.xpos.extended";
        pathToWNFile = "/Users/piek/Desktop/odwn/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.extended.lmf";
        ArrayList<String> relations = new ArrayList<String>();   /// will apply lower-case matching to relation types for building the hiearchy
        relations.add("has_hyperonym");
        relations.add("has_hypernym");
        relations.add("hypernym");
     //   relations.add("event");
     //   relations.add("near_synonym");
     //   relations.add("xpos_near_synonym");
     //   relations.add("has_xpos_hypernym");
     //   relations.add("has_xpos_hyperonym");

        WordnetLmfSaxParser parserWn = new WordnetLmfSaxParser();

        parserWn.setRelations(relations);
        parserWn.parseFile(pathToWNFile);
        parserWn.wordnetData.buildSynsetIndex();
        parserWn.wordnetData.buildLemmaIndex();
        System.out.println("pathToWNFile = " + pathToWNFile);
        System.out.println("parserWn.wordnetData.entryToSynsets.size() = " + parserWn.wordnetData.entryToSynsets.size());
        System.out.println("parserWn.wordnetData.synsetToLexicalUnits.size() = " + parserWn.wordnetData.synsetToLexicalUnits.size());
        System.out.println("parserWn.wordnetData.getHyperRelations().size() = " + parserWn.wordnetData.getHyperRelations().size());
        System.out.println("parserWn.wordnetData.getOtherRelations().size() = " + parserWn.wordnetData.getOtherRelations().size());
        labelWordFile(pathToWordFile,parserWn.wordnetData);
    }

    static void labelWordFile(String filePath, WordnetData wordnetData) {
    try {
        OutputStream fos = new FileOutputStream(filePath+".class-tree");
        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader in = new BufferedReader(isr);
        String inputLine = "";
        while (in.ready()&&(inputLine = in.readLine()) != null) {
            if (inputLine.trim().length()>0) {
                String word = inputLine.trim();
                System.out.println("word = " + word);
                String str = word;
                if (wordnetData.lemmaToSynsets.containsKey(word)) {
                    str += "\n";
                   ArrayList<String> synsets = wordnetData.lemmaToSynsets.get(word);
                   for (int i = 0; i < synsets.size(); i++) {
                       String synset = synsets.get(i);
                       ArrayList<ArrayList<String>> hyperChains = new ArrayList<ArrayList<String>>();
                       wordnetData.getMultipleHyperChain(synset, hyperChains);
                       for (int j = 0; j < hyperChains.size(); j++) {
                           ArrayList<String> chain = hyperChains.get(j);
                           str += "\t"+chain.toString()+"\n";
                       }
/*
                       ArrayList<String> hyperChains = new ArrayList<String>();
                       hyperChains.add(synset);
                       wordnetData.getSingleHyperChain(synset, hyperChains);
                       str += "\t"+hyperChains.toString()+"\n";
*/
                   }
                }
                else {
                   str += ":NOTINWN\n";
                }
                fos.write(str.getBytes());
            }
        }
        in.close();
        fos.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
