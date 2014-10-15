package vu.wntools.wnsimilarity.main;

import vu.wntools.wordnet.WordnetData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 10/14/14.
 */
public class GetLowestCommonSubsumer {


    static void addToCount (ArrayList<Integer> levelCount, int i) {
        while (i>=levelCount.size()) {
            levelCount.add(0);
        }
        Integer cnt = levelCount.get(i);
        cnt++;
        levelCount.set(i, cnt);
    }

    static public String GetLowestCommonSubsumer (WordnetData wordnetData, ArrayList<String> synsets) {
        String lcs = "";
        HashMap<String, ArrayList<Integer>> synsetCount = new HashMap<String, ArrayList<Integer>>();
        for (int i = 0; i < synsets.size(); i++) {
            String synset = synsets.get(i);
            ArrayList<String> coveredHypers = new ArrayList<String>();
            ArrayList<ArrayList<String>> hyperChains = new ArrayList<ArrayList<String>>();
            wordnetData.getMultipleHyperChain(synset, hyperChains);
            ///breadth first
            ///makes sure the lowest occurrences of a hyper is counted
            boolean hasMoreHypers = true;
            int level = 0;
            while (hasMoreHypers) {
                hasMoreHypers = false;
                for (int j = 0; j < hyperChains.size(); j++) {
                    ArrayList<String> strings = hyperChains.get(j);
                    if (level < strings.size()) {
                        String hyper = strings.get(level);
                        if (!coveredHypers.contains(hyper)) {
                            coveredHypers.add(hyper); /// make sure every hyper is counted once
                            if (synsetCount.containsKey(hyper)) {
                                ArrayList<Integer> levelCounts = synsetCount.get(hyper);
                                addToCount(levelCounts, level);
                                synsetCount.put(hyper, levelCounts);
                            } else {
                                ArrayList<Integer> levelCounts = new ArrayList<Integer>();
                                addToCount(levelCounts, level);
                                synsetCount.put(hyper, levelCounts);
                            }
                        }
                        if (strings.size()>level+1) {
                            hasMoreHypers = true;
                        }
                    }
                }
            }
            ///depth first
            /*for (int j = 0; j < hyperChains.size(); j++) {
                ArrayList<String> strings = hyperChains.get(j);
                for (int k = 0; k < strings.size(); k++) {
                    String hyper = strings.get(k);
                    if (!coveredHypers.contains(hyper)) {
                        coveredHypers.add(hyper); /// make sure every hyper is counted once
                        if (synsetCount.containsKey(hyper)) {
                            ArrayList<Integer> levelCounts = synsetCount.get(hyper);
                            addToCount(levelCounts, k);
                            synsetCount.put(hyper, levelCounts);
                        } else {
                            ArrayList<Integer> levelCounts = new ArrayList<Integer>();
                            levelCounts.add(1);
                            synsetCount.put(hyper, levelCounts);
                        }
                    }
                }
            }*/
        }

        Set keySet = synsetCount.keySet();
        Iterator<String> keys = keySet.iterator() ;
        while (keys.hasNext()) {
            String key = keys.next();
            ArrayList<Integer> levelCount = synsetCount.get(key);
            for (int i = 0; i < levelCount.size(); i++) {
                Integer integer = levelCount.get(i);
                if (integer.equals(synsets.size())) {
                    /// this is a hyper that occurs for all the synsets
                    lcs = key+":"+i;
                    /// we stop for the first which should be the lowest
                }
            }
        }
        return lcs;
    }


}
