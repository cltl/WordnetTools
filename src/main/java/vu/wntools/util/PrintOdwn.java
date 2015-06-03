package vu.wntools.util;

import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by piek on 09/04/15.
 */
public class PrintOdwn {

    static public void main (String[] args) {


        String pathToFile = "";
        String pathToWNFile = "";
        //pathToFile = "/Tools/wordnet-tools.0.1/resources/cornetto2.1.lmf.xml";
        pathToWNFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        pathToFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf.test";
        //pathToFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf";
        ArrayList<String> relations = new ArrayList<String>();
        //relations.add("NEAR_SYNONYM");
        relations.add("HAS_HYPERONYM");
        relations.add("HAS_HYPERNYM");
        relations.add("HYPERNYM");
        //relations.add("HAS_MERO_PART");
        //relations.add("HAS_HOLO_PART");

        WordnetLmfSaxParser parserOdwn = new WordnetLmfSaxParser();
        WordnetLmfSaxParser parserWn = new WordnetLmfSaxParser();
        //parser.setPos("v");
        //parser.setRelations(relations);
        parserOdwn.parseFile(pathToFile);
        parserWn.parseFile(pathToWNFile);
/*
        int depth = parser.wordnetData.getAverageDepthByWord();
        System.out.println("depth = " + depth);
*/
        parserOdwn.wordnetData.buildSynsetIndex();
        parserWn.wordnetData.buildSynsetIndex();
        System.out.println("pathToFile = " + pathToFile);
        System.out.println("parser.wordnetData.entryToSynsets.size() = " + parserOdwn.wordnetData.entryToSynsets.size());
        System.out.println("parser.wordnetData.synsetToLexicalUnits.size() = " + parserOdwn.wordnetData.synsetToLexicalUnits.size());
        System.out.println("parser.wordnetData.getHyperRelations().size() = " + parserOdwn.wordnetData.getHyperRelations().size());
        System.out.println("parser.wordnetData.getOtherRelations().size() = " + parserOdwn.wordnetData.getOtherRelations().size());
        System.out.println("pathToWNFile = " + pathToWNFile);
        System.out.println("parserWn.wordnetData.entryToSynsets.size() = " + parserWn.wordnetData.entryToSynsets.size());
        System.out.println("parserWn.wordnetData.synsetToLexicalUnits.size() = " + parserWn.wordnetData.synsetToLexicalUnits.size());
        System.out.println("parserWn.wordnetData.getHyperRelations().size() = " + parserWn.wordnetData.getHyperRelations().size());
        System.out.println("parserWn.wordnetData.getOtherRelations().size() = " + parserWn.wordnetData.getOtherRelations().size());


        try {
            OutputStream fos = new FileOutputStream(pathToFile+".tree");
            printTree(parserOdwn.wordnetData, parserWn.wordnetData,"n", fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<String> getPosSynsets (ArrayList<String> synsetIds, String pos) {
        ArrayList<String> posSynsets = new ArrayList<String>();
        for (int i = 0; i < synsetIds.size(); i++) {
            String s = synsetIds.get(i);
            if (s.endsWith(pos)) {
                posSynsets.add(s);
            }
        }
        return posSynsets;
    }



    static public void printTree (WordnetData wordnetData,WordnetData pwnData, String pos, OutputStream fos) throws IOException {
        // wordnetData.buildSynsetIndex();
        wordnetData.buildChildRelationsFromids();
        ArrayList<String> topNodes = wordnetData.getTopNodes();
        // System.out.println("topNodes.toString() = " + topNodes.toString());
        if (pos.equalsIgnoreCase("n")) {
            ArrayList<String> topNouns = getPosSynsets(topNodes, "n");
            printTree(wordnetData, pwnData,topNouns, pos, fos);
        }
        else if (pos.equalsIgnoreCase("v")) {
            ArrayList<String> topVerbs = getPosSynsets(topNodes, "v");
            printTree(wordnetData, pwnData,topVerbs, pos, fos);
        }
    }

    static void printTree (WordnetData wordnetData,WordnetData pwnData,
                                ArrayList<String> topNodes,
                                String pos, OutputStream fos)  throws IOException {
        ArrayList<String> coveredSynsets = new ArrayList<String>();
        printTree(wordnetData,pwnData, pos, topNodes, 0, coveredSynsets, fos);
    }
    static void printTree (WordnetData wordnetData,WordnetData pwnData,
                          String pos,
                          ArrayList<String> synsets,
                          int level,
                          ArrayList<String> coveredNodes, OutputStream fos) throws IOException {
        for (int i = 0; i < synsets.size(); i++) {
            String s = synsets.get(i);
            if (!coveredNodes.contains(s)) {
                coveredNodes.add(s);
                String str = "";
                for (int j = 0; j < level; j++) {
                    str += "-";
                }
                String synonyms = wordnetData.getSynsetString(s);
                if (synonyms.isEmpty()) {
                    synonyms ="PWN:"+ pwnData.getSynsetString(s);
                }
                str += s +":"+synonyms+ "\n";
                fos.write(str.getBytes());
                // System.out.println("s = " + s);
                if (wordnetData.childRelations.containsKey(s)) {
                    ArrayList<String> children = wordnetData.childRelations.get(s);
                    ArrayList<String> posChildren = getPosSynsets(children, pos);
                    printTree(wordnetData, pwnData, pos, posChildren, level++, coveredNodes, fos);
                }
            }
        }
    }
}
