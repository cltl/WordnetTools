package vu.wntools.util;

import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by piek on 07/07/15.
 */
public class PrintWnLmf {
    static public void main (String[] args) {


        String pathToWNFile = "";
        pathToWNFile = "/Users/piek/Desktop/NWR/NWR-benchmark/coreference/vua-eventcoreference_v2_2014/resources/wneng-30.lmf.xml.xpos.extended";
        ArrayList<String> relations = new ArrayList<String>();
        //relations.add("NEAR_SYNONYM");
        relations.add("HAS_HYPERONYM");
        relations.add("HAS_HYPERNYM");
        relations.add("HYPERNYM");
        //relations.add("HAS_MERO_PART");
        //relations.add("HAS_HOLO_PART");

        WordnetLmfSaxParser parserWn = new WordnetLmfSaxParser();

        parserWn.parseFile(pathToWNFile);
        parserWn.setRelations(relations);
/*
        int depth = parser.wordnetData.getAverageDepthByWord();
        System.out.println("depth = " + depth);
*/
        parserWn.wordnetData.buildSynsetIndex();
        System.out.println("pathToWNFile = " + pathToWNFile);
        System.out.println("parserWn.wordnetData.entryToSynsets.size() = " + parserWn.wordnetData.entryToSynsets.size());
        System.out.println("parserWn.wordnetData.synsetToLexicalUnits.size() = " + parserWn.wordnetData.synsetToLexicalUnits.size());
        System.out.println("parserWn.wordnetData.getHyperRelations().size() = " + parserWn.wordnetData.getHyperRelations().size());
        System.out.println("parserWn.wordnetData.getOtherRelations().size() = " + parserWn.wordnetData.getOtherRelations().size());


        try {
            OutputStream fos = new FileOutputStream(pathToWNFile+".tree");
            printTree(parserWn.wordnetData,"eso:", fos);
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
            else if (s.startsWith(pos)){
                posSynsets.add(s);
            }
        }
        return posSynsets;
    }



    static public void printTree (WordnetData wordnetData, String pos, OutputStream fos) throws IOException {
        // wordnetData.buildSynsetIndex();
        wordnetData.buildChildRelationsFromids();
        ArrayList<String> topNodes = wordnetData.getTopNodesFromIds();
        ArrayList<String> posTops = getPosSynsets(topNodes, pos);
        System.out.println("posTops.toString() = " + posTops.toString());
        printTree(wordnetData,posTops, fos);

    }

    static void printTree (WordnetData wordnetData,
                           ArrayList<String> topNodes, OutputStream fos)  throws IOException {
        ArrayList<String> coveredSynsets = new ArrayList<String>();
        printTree(wordnetData, topNodes, 0, coveredSynsets, fos);
    }
    static void printTree (WordnetData wordnetData,
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
                str += s +":"+synonyms+ "\n";
                fos.write(str.getBytes());
                if (wordnetData.childRelations.containsKey(s)) {
                    ArrayList<String> children = wordnetData.childRelations.get(s);
                    int nLevel = level+1;
                    printTree(wordnetData, children, nLevel, coveredNodes, fos);
                }
            }
        }
    }
}
