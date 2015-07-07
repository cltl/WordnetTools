package vu.wntools.util;

import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 09/04/15.
 */
public class GetLevelStats {
    static public void main (String[] args) {
        //String pathToFile = args[0];
        // String pathToFile = "/Releases/wordnetsimilarity_v.0.1/resources/cornetto2.0.lmf.xml";
        //String pathToFile = "/Tools/wordnet-tools.0.1/resources/cornetto2.1.lmf.xml";
        String pathToFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        ArrayList<String> relations = new ArrayList<String>();
        //relations.add("NEAR_SYNONYM");
        relations.add("HAS_HYPERONYM");
        //relations.add("HAS_MERO_PART");
        //relations.add("HAS_HOLO_PART");

        WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
        //parser.setPos("v");
        //parser.setRelations(relations);

        parser.parseFile(pathToFile);
/*
        int depth = parser.wordnetData.getAverageDepthByWord();
        System.out.println("depth = " + depth);
*/
        parser.wordnetData.buildSynsetIndex();
/*
        if (parser.wordnetData.entryToSynsets.containsKey("person")) {
            System.out.println("HAS IT");
        }
*/
        System.out.println("parser.wordnetData.entryToSynsets.size() = " + parser.wordnetData.entryToSynsets.size());
        System.out.println("parser.wordnetData.getHyperRelations().size() = " + parser.wordnetData.getHyperRelations().size());
        System.out.println("parser.wordnetData.getOtherRelations().size() = " + parser.wordnetData.getOtherRelations().size());
        getStats(parser.wordnetData);
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
    static public void getStats (WordnetData wordnetData) {
        // wordnetData.buildSynsetIndex();
        wordnetData.buildChildRelationsFromids();
        ArrayList<String> topNodes = wordnetData.getTopNodes();
        // System.out.println("topNodes.toString() = " + topNodes.toString());
        ArrayList<String> topNouns = getPosSynsets(topNodes, "n");
        ArrayList<String> topVerbs = getPosSynsets(topNodes, "v");
        ArrayList<String> topAdjectives = getPosSynsets(topNodes, "a");
        //ArrayList<String> topAdverbs = getPosSynsets(topNodes, "b");
        System.out.println("topNouns = " + topNouns.size());
        getStatsForPos(wordnetData, topNouns, "n");
        System.out.println("topVerbs = " + topNouns.size());
        getStatsForPos(wordnetData, topVerbs, "v");
        System.out.println("topAdjectives = " + topAdjectives.size());
        getStatsForPos(wordnetData, topAdjectives, "a");
/*
        System.out.println("topAdverbs = " + topAdverbs);
        getStatsForPos(wordnetData, topAdverbs, "b");
*/
    }

    static void getStatsForPos (WordnetData wordnetData,
                                ArrayList<String> topNodes,
                                String pos) {
        ArrayList<String> coveredSynsets = new ArrayList<String>();
        HashMap<Integer, Integer> levelCounts = new HashMap<Integer, Integer>();
        getStats(wordnetData, pos, topNodes, 0, levelCounts, coveredSynsets);
        Set keySet = levelCounts.keySet();
        Iterator<Integer> keys = keySet.iterator();
        while (keys.hasNext()) {
            Integer key = keys.next();
            Integer count = levelCounts.get(key);
            System.out.println(key+"\t"+count);
        }

    }
    static void getStats (WordnetData wordnetData,
                          String pos,
                          ArrayList<String> synsets,
                          int level,
                          HashMap<Integer, Integer> levelCounts,
                          ArrayList<String> coveredNodes) {
        // System.out.println("level = " + level+"\t"+synsets.size());
        if (levelCounts.containsKey(level)) {
            Integer count = levelCounts.get(level);
            count += synsets.size();
            levelCounts.put(level, count);
        }
        else {
            levelCounts.put(level, synsets.size());
        }
        for (int i = 0; i < synsets.size(); i++) {
            String s = synsets.get(i);
            if (!coveredNodes.contains(s)) {
                coveredNodes.add(s);
                // System.out.println("s = " + s);
                if (wordnetData.childRelations.containsKey(s)) {
                    ArrayList<String> children = wordnetData.childRelations.get(s);
                    ArrayList<String> posChildren = getPosSynsets(children, pos);
                    getStats(wordnetData, pos, posChildren, level + 1, levelCounts, coveredNodes);
                }
            }
        }
    }
}
