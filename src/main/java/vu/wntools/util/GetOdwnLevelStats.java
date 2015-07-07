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
public class GetOdwnLevelStats {

    static String idFilter = "";
    static boolean EMPTYSYNSETS = false;
    static boolean NONEMPTYSYNSETS = false;
    static public void main (String[] args) {
        //idFilter = "odwn";
        EMPTYSYNSETS = true;
       // NONEMPTYSYNSETS = true;


        String pathToFile = "";
        //pathToFile = "/Tools/wordnet-tools.0.1/resources/cornetto2.1.lmf.xml";
        //pathToFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        pathToFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf";
        //pathToFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf";
        ArrayList<String> relations = new ArrayList<String>();
        relations.add("has_hyperonym");
        relations.add("has_hypernym");
        relations.add("hypernym");

        WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
        //parser.setPos("v");
        parser.setRelations(relations);

        parser.provenanceFilter = "pwn";
        parser.parseFile(pathToFile);

        parser.wordnetData.buildSynsetIndex();
        System.out.println("pathToFile = " + pathToFile);
        System.out.println("parser.wordnetData.entryToSynsets.size() = " + parser.wordnetData.entryToSynsets.size());
        System.out.println("parser.wordnetData.getHyperRelations().size() = " + parser.wordnetData.getHyperRelations().size());
        System.out.println("parser.wordnetData.getOtherRelations().size() = " + parser.wordnetData.getOtherRelations().size());
        parser.wordnetData.buildLexicalUnitIndex();

        getOdwnStatsAverageDepth(parser.wordnetData);
       //
        getStatsAverageDepth(parser.wordnetData);
    }

    static public void getOdwnStatsAverageDepth (WordnetData wordnetData) {
        int [] levelcountsN = new int[50];
        for (int i = 0; i < levelcountsN.length; i++) {
            levelcountsN[i] = 0;
        }
        int [] levelcountsV = new int[50];
        for (int i = 0; i < levelcountsV.length; i++) {
            levelcountsV[i] = 0;
        }
        int [] levelcountsNodwn = new int[50];
        for (int i = 0; i < levelcountsNodwn.length; i++) {
            levelcountsNodwn[i] = 0;
        }
        int [] levelcountsVodwn = new int[50];
        for (int i = 0; i < levelcountsVodwn.length; i++) {
            levelcountsVodwn[i] = 0;
        }
        int [] levelcountsNpwn = new int[50];
        for (int i = 0; i < levelcountsNpwn.length; i++) {
            levelcountsNpwn[i] = 0;
        }
        int [] levelcountsVpwn = new int[50];
        for (int i = 0; i < levelcountsVpwn.length; i++) {
            levelcountsVpwn[i] = 0;
        }
        int [] levelcountsN_nl_syn = new int[50];
        for (int i = 0; i < levelcountsN_nl_syn.length; i++) {
            levelcountsN_nl_syn[i] = 0;
        }
        int [] levelcountsV_nl_syn = new int[50];
        for (int i = 0; i < levelcountsV_nl_syn.length; i++) {
            levelcountsV_nl_syn[i] = 0;
        }
        int [] levelcountsN_nl_nosyn = new int[50];
        for (int i = 0; i < levelcountsN_nl_nosyn.length; i++) {
            levelcountsN_nl_nosyn[i] = 0;
        }
        int [] levelcountsV_nl_nosyn = new int[50];
        for (int i = 0; i < levelcountsV_nl_nosyn.length; i++) {
            levelcountsV_nl_nosyn[i] = 0;
        }
        int nSyns = 0;
        int nNoSyns = 0;
        for (int i = 0; i < wordnetData.synsetArrayList.size(); i++) {
            String synsetId = wordnetData.synsetArrayList.get(i);
            String synonyms = wordnetData.getSynsetString(synsetId);
            if (synonyms.isEmpty()) {
                nNoSyns++;
            }
            else {
                nSyns++;
            }
            int depth = wordnetData.getAverageDepthBySynset(synsetId);
            if (synonyms.isEmpty()) {
                System.out.println("s = " + synsetId);
                System.out.println("depth = " + depth);
            }
            if (depth>-1 && depth<50) {
                if (synsetId.endsWith("n")) {
                    levelcountsN[depth] = levelcountsN[depth]+1;
                  //  System.out.println("levelcountsN = " + levelcountsN[depth]);
                    if (synsetId.startsWith("odwn")) {
                        levelcountsNodwn[depth] = levelcountsNodwn[depth]+1;
                    }
                    else if (synsetId.startsWith("eng")) {
                        levelcountsNpwn[depth] = levelcountsNpwn[depth]+1;
                    }

                    if (synonyms.isEmpty()) {
                        levelcountsN_nl_nosyn[depth] = levelcountsN_nl_nosyn[depth]+1;
                        System.out.println("levelcountsN_nl_nosyn[depth] = " + levelcountsN_nl_nosyn[depth]);
                    }
                    else {
                        levelcountsN_nl_syn[depth] = levelcountsN_nl_syn[depth]+1;
                    }
                }
                else if (synsetId.endsWith("v")) {
                    levelcountsV[depth] = levelcountsV[depth]+1;
                    if (synsetId.startsWith("odwn")) {
                        levelcountsVodwn[depth] = levelcountsVodwn[depth]+1;
                    }
                    else if (synsetId.startsWith("eng")) {
                        levelcountsVpwn[depth] = levelcountsVpwn[depth]+1;
                    }

                    if (synonyms.isEmpty()) {
                        levelcountsV_nl_nosyn[depth] = levelcountsV_nl_nosyn[depth]+1;
                    }
                    else {
                        levelcountsV_nl_syn[depth] = levelcountsV_nl_syn[depth]+1;
                    }
                }
            }
            // System.out.println("synsetId = " + synsetId);
        }
        System.out.println("nSyns = " + nSyns);
        System.out.println("nNoSyns = " + nNoSyns);

        for (int i = 0; i < levelcountsN.length; i++) {
            System.out.println( i +"\t"+ levelcountsN[i]+"\t"+levelcountsNodwn[i]+"\t"+levelcountsNpwn[i]+ "\t"+levelcountsN_nl_nosyn[i]+"\t"+levelcountsN_nl_syn[i]);
        }
        for (int i = 0; i < levelcountsV.length; i++) {
            System.out.println( i +"\t"+ levelcountsV[i]+"\t"+levelcountsVodwn[i]+"\t"+levelcountsVpwn[i]+ "\t"+levelcountsV_nl_nosyn[i]+"\t"+levelcountsV_nl_syn[i]);
        }

    }

    static public void getStatsAverageDepth (WordnetData wordnetData) {
        int [] levelcountsN = new int[50];
        for (int i = 0; i < levelcountsN.length; i++) {
            levelcountsN[i] = 0;
        }
        int [] levelcountsV = new int[50];
        for (int i = 0; i < levelcountsV.length; i++) {
            levelcountsV[i] = 0;
        }
        for (int i = 0; i < wordnetData.synsetArrayList.size(); i++) {
            String synsetId = wordnetData.synsetArrayList.get(i);
            int depth = wordnetData.getAverageDepthBySynset(synsetId);
            if (depth>-1 && depth <50) {
                if (synsetId.endsWith("n")) {
                    levelcountsN[depth] = levelcountsN[depth]+1;
                }
                else if (synsetId.endsWith("v")) {
                    levelcountsV[depth] = levelcountsV[depth]+1;
                }
            }
            // System.out.println("synsetId = " + synsetId);
        }

        for (int i = 0; i < levelcountsN.length; i++) {
            System.out.println( i +"\t"+ levelcountsN[i]);
        }
        for (int i = 0; i < levelcountsV.length; i++) {
            System.out.println( i +"\t"+ levelcountsV[i]);
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
        int countSynsets = 0;
        if (!idFilter.isEmpty()) {
            for (int i = 0; i < synsets.size(); i++) {
                String s = synsets.get(i);
                if (s.startsWith(idFilter)) {
                    countSynsets++;
                }
            }
        }
        else if (EMPTYSYNSETS){
            for (int i = 0; i < synsets.size(); i++) {
                String s = synsets.get(i);
                String synonyms = wordnetData.getSynsetString(s);
                if (synonyms.isEmpty()) {
                    countSynsets++;
                }
            }
        }
        else if (NONEMPTYSYNSETS){
            for (int i = 0; i < synsets.size(); i++) {
                String s = synsets.get(i);
                String synonyms = wordnetData.getSynsetString(s);
                if (!synonyms.isEmpty()) {
                    countSynsets++;
                }
            }
        }
        else {
            countSynsets = synsets.size();
        }

        if (levelCounts.containsKey(level)) {
            Integer count = levelCounts.get(level);
            count += countSynsets;
            levelCounts.put(level, count);
        }
        else {
            levelCounts.put(level, countSynsets);
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
